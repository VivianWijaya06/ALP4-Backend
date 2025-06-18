document.addEventListener('DOMContentLoaded', function() {
    // Fetch available songs from the backend
    fetch('/api/files/songs/list')
        .then(response => response.json())
        .then(songs => {            if (songs.length === 0) {
                // Fallback to hardcoded list if no songs are available
                songs = [
                    "/api/files/songs/morning-garden-acoustic-chill-15013.mp3",
                    "/api/files/songs/ice-latte-lofi-chill-calm-music-360898.mp3",
                    "/api/files/songs/chill-beats-185843.mp3"
                ];
            }
            
            initializeRandomSongs(songs);
        })
        .catch(error => {
            console.error('Error fetching songs:', error);            // Fallback to hardcoded list
            const fallbackSongs = [
                "/api/files/songs/morning-garden-acoustic-chill-15013.mp3",
                "/api/files/songs/ice-latte-lofi-chill-calm-music-360898.mp3",
                "/api/files/songs/chill-beats-185843.mp3"
            ];
            initializeRandomSongs(fallbackSongs);
        });    function initializeRandomSongs(songs) {
        function getRandomSongs(count) {
            // Use Math.random() to generate different songs on each visit
            const shuffled = [...songs];
            for (let i = shuffled.length - 1; i > 0; i--) {
                const j = Math.floor(Math.random() * (i + 1));
                [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
            }
            return shuffled.slice(0, count);
        }function getSongTitle(filePath) {
            let songName = filePath.split('/').pop();
            // Remove file extension
            songName = songName.replace(/\.mp3$/, '');
            // Replace hyphens and underscores with spaces
            songName = songName.replace(/[-_]/g, ' ');
            // Remove numbers at the end (like -15013, -270241, etc.)
            songName = songName.replace(/\s+\d+$/, '');
            // Capitalize first letter of each word
            songName = songName.replace(/\b\w/g, l => l.toUpperCase());
            return decodeURIComponent(songName);
        }        const randomSongs = getRandomSongs(3);

        const musicPlayerContainer = document.getElementById('music-player-container');
        if (musicPlayerContainer) {
            musicPlayerContainer.innerHTML = '';

            randomSongs.forEach(songSrc => {
                const songName = getSongTitle(songSrc);                const playerHtml = `
                    <div class="bg-gray-50 rounded-lg p-4 mb-4">
                        <div class="flex items-center mb-3">
                            <img src="https://img.icons8.com/color/48/000000/apple-music.png" alt="Apple Music" class="w-10 h-10 mr-3">
                            <h4 class="font-semibold text-gray-700 truncate">${songName}</h4>
                        </div>
                        <audio controls class="w-full mt-2">
                            <source src="${songSrc}" type="audio/mpeg">
                            Your browser does not support the audio element.
                        </audio>
                    </div>
                `;
                musicPlayerContainer.insertAdjacentHTML('beforeend', playerHtml);
            });

            // Add audio control functionality after creating players
            setTimeout(() => {
                const audioPlayers = document.querySelectorAll('#music-player-container audio');
                
                audioPlayers.forEach(player => {
                    player.addEventListener('play', function() {
                        audioPlayers.forEach(otherPlayer => {
                            if (otherPlayer !== player && !otherPlayer.paused) {
                                otherPlayer.pause();
                            }
                        });
                    });

                    player.volume = localStorage.getItem('audioVolume') || 0.7;
                    
                    player.addEventListener('volumechange', function() {
                        localStorage.setItem('audioVolume', player.volume);
                    });
                });
            }, 100);
        } else {
            console.error('Music player container not found');
        }
    }
});