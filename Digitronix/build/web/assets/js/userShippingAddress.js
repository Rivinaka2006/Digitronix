function loadUserData() {
    loadShippingData();
    getCityData();
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

async function saveShippingData() {
    const firstName = document.getElementById("firstName").value??"";
    const lastName = document.getElementById("lastName").value;
    const streetAddress = document.getElementById("street").value;
    const streetAddress1 = document.getElementById("street1").value;
    const cityId = document.getElementById("citySelect").value;
    const postcode = document.getElementById("postcode").value;

    const shippingData = {
        firstName,
        lastName,
        streetAddress,
        streetAddress1,
        cityId,
        postcode
    };

    const shippingDataJSON = JSON.stringify(shippingData);

    const response = await fetch("UserShippingAddress", {
        method: "PUT",
        headers: {
            "content-Type": "application/json"
        },
        body: shippingDataJSON
    });

    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.status) {
            loadShippingData();
            window.dispatchEvent(new Event("load"));
        } else {
            document.getElementById("message").innerHTML = json.message;
        }
    } else {
        document.getElementById("message").innerHTML = "Shipping details update failed";
    }
}

async function loadShippingData() {

    const response = await fetch("UserShippingAddress");

    if (response.ok) {
        const json = await response.json();
        //console.log(json);
        document.getElementById("firstName").value = json.firstName ?? "";
        document.getElementById("lastName").value = json.lastName ?? "";
        document.getElementById("street").value = json.streetAddress ?? "";
        document.getElementById("street1").value = json.streetAddress1 ?? "";
        document.getElementById("postcode").value = json.postcode ?? "";
        //document.getElementById("city").value = json.city;
//        document.getElementById("mobile").value = json.mobile;
//        document.getElementById("email").value = json.email;
    } else {
        console.log("No user data found");
    }
}