
const email = document.getElementById('emailText');
const password = document.getElementById('passwordText');
const btn = document.getElementById('loginBtn');

var myHeaders = new Headers();
myHeaders.append("Content-Type", "application/json");
myHeaders.append("Cookie", "JSESSIONID=089AE973B02E12229DD40E52BB47DFA8");

var raw = JSON.stringify({
    "email": email,
    "password": password
});

var requestOptions = {
    method: 'POST',
    headers: myHeaders,
    body: raw,
    redirect: 'follow'
};

btn.onclick = function() {

    fetch("localhost:8080/api/user/login", requestOptions)
        .then(response => response.text())
        .then(result => console.log(result))
        .catch(error => console.log('error', error));

    localStorage.setItem("userID", response.body.userID);

}