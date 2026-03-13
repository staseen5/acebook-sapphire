document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.mention-content').forEach( (el)  => {
        const raw = el.textContent;
        el.innerHTML = raw.replace(/@(\w+)/g,  (match, username) => {
            return '<a href="/profile/' + encodeURIComponent(username) + '">@' + username + '</a>';
        });
    });
});