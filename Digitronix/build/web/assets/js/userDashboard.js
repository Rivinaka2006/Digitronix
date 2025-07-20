function loadData() {
    getUserData();
}

async function getUserData() {
    const response = await fetch("UserProfile");

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        document.getElementById("username").innerHTML = `Hello ${json.userName}`;
        document.getElementById("usernameDisplay").textContent = `(Not ${json.userName})?`;
        document.getElementById("firstName").value = json.firstName;
        document.getElementById("lastName").value = json.lastName;
        document.getElementById("displayName").value = json.userName;
        document.getElementById("email").value = json.email;
    } else {
        console.log("No user data found");
    }
}
