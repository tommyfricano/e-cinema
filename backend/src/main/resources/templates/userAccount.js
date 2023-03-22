const firstname = document.getElementById('firstName');
const lastName = document.getElementById('lastName');
const phone = document.getElementById('phone');
const dob = document.getElementById('dob');

const user = localStorage.getItem("userID");

var myHeaders = new Headers();
myHeaders.append("Cookie", "JSESSIONID=089AE973B02E12229DD40E52BB47DFA8");

var requestOptions = {
    method: 'GET',
    headers: myHeaders,
    redirect: 'follow'
};

fetch("localhost:8080/api/user/" + user.toString(), requestOptions)
    .then(response => response.text())
    .then(result => console.log(result))
    .catch(error => console.log('error', error));

