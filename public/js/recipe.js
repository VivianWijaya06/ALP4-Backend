// Fungsi global untuk mendapatkan ID resep berdasarkan judul dari backend
async function getRecipeIdByTitle(title) {
    const res = await fetch('/api/recipes');
    if (!res.ok) throw new Error('Gagal mengambil daftar resep');
    const recipes = await res.json();
    const found = recipes.find(r => r.title && r.title.trim().toLowerCase() === title.trim().toLowerCase());
    if (!found) throw new Error('Resep tidak ditemukan di database');
    return found.id;
}