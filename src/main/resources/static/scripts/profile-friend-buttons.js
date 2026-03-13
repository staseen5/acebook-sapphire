const addFriendBtn = document.getElementById("add-friend-btn");
const rmvFriendBtn = document.getElementById("rmv-friend-btn");

addFriendBtn.addEventListener("click", e => {
    // Send friend request from current user to user id
    rmvFriendBtn.style.display = "block"
    addFriendBtn.style.display = "none"
})

rmvFriendBtn.addEventListener("click", e => {
    // Remove user by id from friend list
    addFriendBtn.style.display = "block"
    rmvFriendBtn.style.display = "none"
})

const blockUserBtn = document.getElementById("block-user-btn");
const unblockUserBtn = document.getElementById("unblock-user-btn");

blockUserBtn.addEventListener("click", e => {
    // Send friend request from current user to user id
    unblockUserBtn.style.display = "block"
    blockUserBtn.style.display = "none"
})

unblockUserBtn.addEventListener("click", e => {
    // Remove user by id from friend list
    blockUserBtn.style.display = "block"
    unblockUserBtn.style.display = "none"
})

// NEED TO REPLACE TO DYNAMICALLY SHOW CORRECT BUTTONS BASED ON FRIENDSHIP DATA
window.onload = function() {
    unblockUserBtn.style.display = "none"
    rmvFriendBtn.style.display = "none"
};