async function logOut() {
    const response = await fetch("LogOut");

    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            window.location = "login.html";
        } else {
            window.location.reload();
        }
    } else {
        console.log("Logout Failed!");
    }
}
