// Daily Menu Randomization
class DailyMenuManager {
    constructor() {
        this.recipes = [
            {
                name: "Nasi Goreng Spesial",
                description: "Dengan telur mata sapi, ayam suwir, dan kerupuk",
                time: "40 menit",
                difficulty: "Normal",
                servings: "4 porsi",
                link: "nasgor.html"
            },
            {
                name: "Sup Ayam Jahe",
                description: "Hangatkan badan dengan kuah kaldu ayam dan jahe segar",
                time: "45 menit",
                difficulty: "Sedang",
                servings: "6 porsi",
                link: "SupAyamJahe.html"
            },
            {
                name: "Salad Segar",
                description: "Campuran sayuran segar dengan dressing lemon-madu",
                time: "15 menit",
                difficulty: "Mudah",
                servings: "2 porsi",
                link: "salad.html"
            },
            {
                name: "Pancake",
                description: "Resep pancake klasik yang lembut dan mengembang",
                time: "15 menit",
                difficulty: "Mudah",
                servings: "2 porsi",
                link: "pancake.html"
            },
            {
                name: "Spaghetti",
                description: "Pasta Italia dengan saus tomat dan keju parmesan",
                time: "30 menit",
                difficulty: "Sedang",
                servings: "3 porsi",
                link: "spaghetti.html"
            },
            {
                name: "Telur Dadar",
                description: "Telur dadar sederhana dengan bumbu yang lezat",
                time: "10 menit",
                difficulty: "Mudah",
                servings: "2 porsi",
                link: "telurDadar.html"
            },
            {
                name: "Sayur Sop",
                description: "Sup sayuran segar dengan kuah bening yang hangat",
                time: "25 menit",
                difficulty: "Mudah",
                servings: "4 porsi",
                link: "sayurSop.html"
            }
        ];
        
        this.init();
    }

    // Shuffle array using Fisher-Yates algorithm
    shuffleArray(array) {
        const shuffled = [...array];
        for (let i = shuffled.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
        }
        return shuffled;
    }

    // Get random recipes for today's menu
    getRandomMenuItems(count = 4) {
        const shuffledRecipes = this.shuffleArray(this.recipes);
        return shuffledRecipes.slice(0, count);
    }

    // Create HTML for a menu item
    createMenuItemHTML(recipe) {
        return `
            <a href="${recipe.link}" class="flex items-start hover:bg-gray-50 rounded-lg p-2 -mx-2 transition-all duration-200">
                <div>
                    <h4 class="font-semibold text-lg">${recipe.name}</h4>
                    <p class="text-gray-600 text-sm">${recipe.description}</p>
                    <div class="mt-2 flex flex-wrap gap-2">
                        <span class="bg-[#fff4ef] text-[#803f22] px-2 py-1 rounded text-xs">${recipe.time}</span>
                        <span class="bg-[#fff4ef] text-[#803f22] px-2 py-1 rounded text-xs">${recipe.difficulty}</span>
                        <span class="bg-[#fff4ef] text-[#803f22] px-2 py-1 rounded text-xs">${recipe.servings}</span>
                    </div>
                </div>
            </a>
        `;
    }

    // Render the daily menu
    renderDailyMenu() {
        const menuContainer = document.getElementById('daily-menu-container');
        if (!menuContainer) {
            console.error('Daily menu container not found');
            return;
        }

        const randomRecipes = this.getRandomMenuItems(4);
        const menuHTML = randomRecipes.map(recipe => this.createMenuItemHTML(recipe)).join('');
        
        menuContainer.innerHTML = menuHTML;
        
        console.log('Daily menu loaded with recipes:', randomRecipes.map(r => r.name));
    }

    // Initialize the daily menu
    init() {
        document.addEventListener('DOMContentLoaded', () => {
            this.renderDailyMenu();
        });
    }
}

// Initialize the daily menu manager
new DailyMenuManager();
