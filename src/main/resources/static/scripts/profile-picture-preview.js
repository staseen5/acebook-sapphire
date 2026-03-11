const input = document.getElementById("profile-picture");

const addProfilePicDiv = document.getElementById("add-profile-pic-div");
const rmvProfilePicDiv = document.getElementById("rmv-profile-pic-div");

const profilePicPreview = document.getElementById("profile-pic-img");
const rmvProfilePicBtn = document.getElementById("rmv-profile-pic-btn");

input.addEventListener("change", e => {
    const files = e.target.files;
    if(files && files.length > 0) {
        const reader = new FileReader();
        reader.onload = (e) => {
            profilePicPreview.src = e.target.result
            addProfilePicDiv.style.display = "none"
            rmvProfilePicDiv.style.display = "block"
        }

        reader.readAsDataURL(files[0])
    }
})

rmvProfilePicBtn.addEventListener("click", e => {
    input.value = ""
    addProfilePicDiv.style.display = "block"
    rmvProfilePicDiv.style.display = "none"
})
