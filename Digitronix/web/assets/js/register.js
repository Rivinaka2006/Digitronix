async function registerUser() {
    const userName = document.getElementById("register-username").value;
    const email = document.getElementById("register-email").value;
    const password = document.getElementById("register-password").value;

    const user = {
        userName: userName,
        email: email,
        password: password,
        roleId: 2,
        statusId: 1
    };

    const userJson = JSON.stringify(user);
    const response = await fetch(
            "Register",
            {
                method: "POST",
                body: userJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
    if (response.ok) { //success
        const json = await response.json();
        console.log(json);
        if (json.status) { // if true
            window.location.href = "verifyAccount.html";
            //console.log(json);
        } else {// when status false
            document.getElementById("message").innerHTML = json.message;
            //alert(json.message);
        }
    } else {
        document.getElementById("message").innerHTML = "Registration failed. please try again";
        //alert("Registration failed. please try again");
    }
}


