const input = document.getElementById("img-upload");
const label = document.getElementById("file-label");
const preview = document.getElementById("img-preview");
const previewContainer = document.getElementById("img-preview-container");
const removeButton = document.getElementById("remove-img");

input.addEventListener("change", e => {
    const files = e.target.files;
    if(files && files.length > 0) {
        const reader = new FileReader();
        reader.onload = (e) => {
            preview.src = e.target.result
            previewContainer.style.display = "block"
        }

        reader.readAsDataURL(files[0])
        label.classList.add("has-file")
    }
})

removeButton.addEventListener("click", e => {
    input.value = ""
    previewContainer.style.display = "none"
    preview.src = ""
    label.classList.remove("has-file")
})
