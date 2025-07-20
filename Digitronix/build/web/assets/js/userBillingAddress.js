function loadUserData() {
    loadBillingData();
    getCityData();
}

async function loadBillingData() {

    const response = await fetch("userBillingAddress");

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        document.getElementById("firstName").value = json.firstName ?? "";
        document.getElementById("lastName").value = json.lastName ?? "";
        document.getElementById("street").value = json.streetAddress ?? "";
        document.getElementById("citySelect").value = json.city ?? "";
        document.getElementById("mobile").value = json.mobile ?? "";
        document.getElementById("email").value = json.email ?? "";
    } else {
        console.log("No user data found");
    }

}


async function saveBillingData() {
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const streetAddress = document.getElementById("street").value;
    const cityId = document.getElementById("citySelect").value;
    const mobile = document.getElementById("mobile").value;
    const email = document.getElementById("email").value;

    const billingData = {
        firstName,
        lastName,
        streetAddress,
        cityId,
        mobile,
        email
    };

    const billingDataJSON = JSON.stringify(billingData);

    const response = await fetch("userBillingAddress", {
        method: "PUT",
        headers: {
            "content-Type": "application/json"
        },
        body: billingDataJSON
    });

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.status) {
            loadBillingData();
            window.dispatchEvent(new Event("load"));
        } else {
            document.getElementById("message").innerHTML = json.message;
        }
    } else {
        document.getElementById("message").innerHTML = "Billing details update failed";
    }

}

async function getCityData() {
    const response = await fetch("CityData");

    if (response.ok) {
        const json = await response.json();
        const citySelect = document.getElementById("citySelect");

        json.forEach(city => {
            let option = document.createElement("option");
            option.innerHTML = city.name;
            option.value = city.id;
            citySelect.appendChild(option);
        });
    }
}
