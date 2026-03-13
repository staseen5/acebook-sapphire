const openLightbox = (thumb) =>  {
    const lb = document.getElementById('lightbox');
    document.getElementById('lightbox-img').src = thumb.dataset.full;
    document.getElementById('lightbox-caption').textContent = thumb.dataset.caption;
    lb.style.display = 'flex';
}

const closeLightbox = () => {
    document.getElementById('lightbox').style.display = 'none';
}
    document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') closeLightbox();
});
