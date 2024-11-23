
// JavaScript for sending PUT and DELETE requests since HTML forms do not support PUT or DELETE directly
function sendPutRequest(event) {
    event.preventDefault(); // Prevent form submission

    const form = document.getElementById('putForm');
    const data = new URLSearchParams(new FormData(form)).toString();

    fetch(form.action, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: data,
    })
        .then(response => response.text())
        .then(response => alert('Response: ' + response))
        .catch(error => alert('Error: ' + error));
}

function sendDeleteRequest(event) {
    event.preventDefault(); // Prevent form submission

    const form = document.getElementById('deleteForm');
    const data = new URLSearchParams(new FormData(form)).toString();

    fetch(form.action, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: data,
    })
        .then(response => response.text())
        .then(response => alert('Response: ' + response))
        .catch(error => alert('Error: ' + error));
}
