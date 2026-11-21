const API_URL = "http://localhost:8080/api/vehicles";

// Load vehicles when the home page opens
document.addEventListener("DOMContentLoaded", function () {
    loadVehicles();
});


// Fetch available vehicles from Spring Boot
async function loadVehicles(category = "") {

    const vehicleList = document.getElementById("vehicleList");

    vehicleList.innerHTML = `
        <div class="col-12 text-center">
            <p>Loading vehicles...</p>
        </div>
    `;

    try {

        let url = API_URL + "/available";

        if (category) {
            url = API_URL + "/category/" + encodeURIComponent(category);
        }

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error("Unable to load vehicles");
        }

        const vehicles = await response.json();

        // Only display available vehicles
        const availableVehicles = vehicles.filter(
            vehicle => vehicle.status === "AVAILABLE"
        );

        displayVehicles(availableVehicles);

    } catch (error) {

        console.error("Error:", error);

        vehicleList.innerHTML = `
            <div class="col-12 text-center">
                <p class="text-danger">
                    Unable to load vehicles. Please try again later.
                </p>
            </div>
        `;
    }
}


// Display vehicle cards
function displayVehicles(vehicles) {

    const vehicleList = document.getElementById("vehicleList");

    vehicleList.innerHTML = "";

    if (vehicles.length === 0) {

        vehicleList.innerHTML = `
            <div class="col-12 text-center">
                <h5>No available vehicles found.</h5>
            </div>
        `;

        return;
    }

    vehicles.forEach(vehicle => {

        const image = vehicle.imageUrl
            ? escapeHtml(vehicle.imageUrl)
            : "https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?w=800";

        const name = escapeHtml(vehicle.name);
        const brand = escapeHtml(vehicle.brand);
        const model = escapeHtml(vehicle.model);
        const category = escapeHtml(vehicle.category);

        const card = `
            <div class="col-lg-4 col-md-6">

                <div class="vehicle-card">

                    <img src="${image}"
                         alt="${name}"
                         onerror="this.src='https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?w=800'">

                    <div class="vehicle-card-body">

                        <span class="badge bg-light text-dark mb-2">
                            ${category}
                        </span>

                        <h4>${name}</h4>

                        <p>${brand} - ${model}</p>

                        <p>
                            ${vehicle.seatingCapacity} Seats |
                            ${escapeHtml(vehicle.fuelType)} |
                            ${escapeHtml(vehicle.transmission)}
                        </p>

                        <div class="vehicle-price">
                            ₹${Number(vehicle.pricePerDay).toLocaleString("en-IN")}
                            <small class="text-muted">/ day</small>
                        </div>

                        <button class="btn btn-warning"
                                onclick="bookVehicle(${Number(vehicle.id)})">
                            Book Now
                        </button>

                    </div>

                </div>

            </div>
        `;

        vehicleList.innerHTML += card;

    });
}


// Escape text received from API before inserting it into HTML
function escapeHtml(value) {

    return String(value ?? "").replace(/[&<>"']/g, function (character) {

        const entities = {
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        };

        return entities[character];
    });
}


// Search vehicles
function searchVehicles() {

    const category = document.getElementById("category").value;

    const pickupDate = document.getElementById("pickupDate").value;
    const returnDate = document.getElementById("returnDate").value;

    if (pickupDate && returnDate &&
        new Date(returnDate) <= new Date(pickupDate)) {

        alert("Return date must be after pickup date.");
        return;
    }

    if ((pickupDate && !returnDate) || (!pickupDate && returnDate)) {
        alert("Please select both pickup and return dates.");
        return;
    }

    // Current search filters by category.
    // Date-based availability will be added with the booking API later.
    loadVehicles(category);

    document.getElementById("vehicles").scrollIntoView({
        behavior: "smooth"
    });
}


// Book vehicle
function bookVehicle(vehicleId) {

    // Login functionality will be connected in the next stages.
    window.location.href = "login.html?vehicleId=" + vehicleId;
}
