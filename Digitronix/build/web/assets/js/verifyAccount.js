async function verifyAccount() {
    const verificationCode = document.getElementById("verificationCode").value;

    const verification = {
        verificationCode: verificationCode
    };

    const verificationJson = JSON.stringify(verification);

    const response = await fetch("VerifyAccount", {
        method: "POST",
        body: verificationJson,
        headers: {
            "Content-Type": "application/json"
        }
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
//            const loginStatus = document.getElementById("login-Status");
//            console.log(response);
//            loginStatus.innerHTML = "My Account";
//            loginStatus.href = "userProfile.html";  // <-- Update link destination
            window.location = "home.html"; // or redirect directly to profile
        } else {
            if (json.message === "1") {
                window.location = "sign-in.html";
            } else {
                document.getElementById("message").innerHTML = json.message;
            }
        }

    } else {
        document.getElementById("message").innerHTML = "Verification failed. Please try again later";
    }
}




