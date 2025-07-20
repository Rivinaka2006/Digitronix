async function loginUser() {

    const email = document.getElementById("login-email").value;
    const password = document.getElementById("login-password").value;

    const login = {
        email: email,
        password: password
    };

    const loginJson = JSON.stringify(login);

    const response = await fetch(
            "Login",
            {
                method: "POST",
                body: loginJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) { //success
        const json = await response.json();
        if (json.status) { // if true
            if (json.message === "1") {
                window.location = "verifyAccount.html";
            } else {
//                loginStatus.innerHTML = "My Account";
//                loginStatus.href = "userProfile.html";  // <-- Update link destination
                window.location = "home.html";
                //document.getElementById("login-Status").innerHTML = "My Account";
            }
        } else {// when status false
            document.getElementById("message").innerHTML = json.message;
        }
    } else {
        document.getElementById("message").innerHTML = "Sign In failed. please try again";
    }
}

