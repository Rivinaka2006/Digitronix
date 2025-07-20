function loadData() {
    getUserData();
}

async function getUserData() {
    const response = await fetch("UserProfile");

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        document.getElementById("firstName").value = json.firstName ?? "";
        document.getElementById("lastName").value = json.lastName ?? "";
        document.getElementById("displayName").value = json.userName ?? "";
        document.getElementById("email").value = json.email ?? "";
    } else {
        console.log("No user data found");
    }
}

async function updateUserDetails() {
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const userName = document.getElementById("displayName").value;
    const email = document.getElementById("email").value;
    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    const userData = {
        firstName,
        lastName,
        userName,
        email,
        currentPassword,
        newPassword,
        confirmPassword
    };

    const userDetailsJSON = JSON.stringify(userData);

    const response = await fetch("UserProfile", {
        method: "PUT",
        headers: {
            "content-Type": "application/json"
        },
        body: userDetailsJSON
    });

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            getUserData();
            window.dispatchEvent(new Event("load"));
        } else {
            document.getElementById("message").innerHTML = json.message;
        }
    } else {
        document.getElementById("message").innerHTML = "Profile details update failed";
    }
}



