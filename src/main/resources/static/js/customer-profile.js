document.addEventListener("DOMContentLoaded", function () {

    loadCustomerProfile();

    const profileForm = document.getElementById("profileForm");

    profileForm.addEventListener("submit", updateCustomerProfile);

    // Reset form to the saved profile information
    profileForm.addEventListener("reset", function (event) {
        event.preventDefault();
        loadCustomerProfile();
        document.getElementById("profileMessage").innerHTML = "";
    });

});


// Read API response safely
async function readResponse(response) {
    const text = await response.text();

    if (!text) {
        return {};
    }

    try {
        return JSON.parse(text);
    } catch (error) {
        return { message: text };
    }
}


// Load customer profile
async function loadCustomerProfile() {

    try {
        const response = await fetch("/api/customers/profile", {
            method: "GET",
            credentials: "same-origin"
        });

        if (response.status === 401 || response.status === 403) {
            window.location.href = "login.html";
            return;
        }

        const customer = await readResponse(response);

        if (!response.ok) {
            throw new Error(
                customer.message ||
                customer.error ||
                "Unable to load customer profile."
            );
        }

        document.getElementById("name").value =
            customer.name || "";

        document.getElementById("email").value =
            customer.email || "";

        document.getElementById("phone").value =
            customer.phone || "";

        document.getElementById("drivingLicenseNumber").value =
            customer.drivingLicenseNumber || "";

        updateProfileDisplay(customer.name);

    } catch (error) {
        showProfileMessage(error.message, "danger");
    }
}


// Update profile heading and avatar
function updateProfileDisplay(name) {

    const displayName = name || "Customer";

    document.getElementById("profileName").textContent =
        displayName;

    document.getElementById("profileInitial").textContent =
        displayName.charAt(0).toUpperCase();
}


// Update customer profile
async function updateCustomerProfile(event) {

    event.preventDefault();

    const name = document.getElementById("name").value.trim();

    const phone = document.getElementById("phone").value.trim();

    const drivingLicenseNumber =
        document.getElementById("drivingLicenseNumber").value.trim();

    const saveButton = document.getElementById("saveButton");

    if (!name || !phone || !drivingLicenseNumber) {
        showProfileMessage(
            "Please fill in all required fields.",
            "warning"
        );
        return;
    }

    if (!/^[6-9]\d{9}$/.test(phone)) {
        showProfileMessage(
            "Please enter a valid 10-digit Indian mobile number.",
            "warning"
        );
        return;
    }

    try {

        saveButton.disabled = true;
        saveButton.textContent = "Saving...";

        const response = await fetch("/api/customers/profile", {
            method: "PUT",
            credentials: "same-origin",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                name: name,
                phone: phone,
                drivingLicenseNumber: drivingLicenseNumber
            })
        });

        if (response.status === 401 || response.status === 403) {
            window.location.href = "login.html";
            return;
        }

        const data = await readResponse(response);

        if (!response.ok) {
            throw new Error(
                data.message ||
                data.error ||
                "Unable to update profile."
            );
        }

        updateProfileDisplay(data.name || name);

        // Keep the form synchronized with the saved values
        document.getElementById("name").value = data.name || name;
        document.getElementById("phone").value = data.phone || phone;

        document.getElementById("drivingLicenseNumber").value =
            data.drivingLicenseNumber || drivingLicenseNumber;

        showProfileMessage(
            "Your profile has been updated successfully!",
            "success"
        );

    } catch (error) {

        showProfileMessage(error.message, "danger");

    } finally {

        saveButton.disabled = false;
        saveButton.textContent = "Save Changes";
    }
}


// Display success and error messages
function showProfileMessage(message, type) {

    document.getElementById("profileMessage").innerHTML = `
        <div class="alert alert-${type}">
            ${escapeHtml(message)}
        </div>
    `;
}


// Escape HTML
function escapeHtml(value) {

    return String(value).replace(/[&<>"']/g, function (character) {

        return {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        }[character];

    });
}
