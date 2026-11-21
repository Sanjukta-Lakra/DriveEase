document.addEventListener("DOMContentLoaded", function () {

    loadVehicles();
    loadMyReviews();

    document.getElementById("reviewForm").addEventListener("submit", submitReview);

});

// Load vehicles into the dropdown
async function loadVehicles() {

    const vehicleSelect = document.getElementById("vehicleId");

    try {

        const response = await fetch("/api/vehicles");

        if (!response.ok) {
            throw new Error("Unable to load vehicles.");
        }

        const vehicles = await response.json();

        vehicleSelect.innerHTML = '<option value="">Select Vehicle</option>';

        if (!vehicles || vehicles.length === 0) {
            vehicleSelect.innerHTML =
                '<option value="">No vehicles available</option>';
            return;
        }

        vehicles.forEach(vehicle => {

            const option = document.createElement("option");

            option.value = vehicle.id;

            const vehicleName = vehicle.name ||
                `${vehicle.brand || ""} ${vehicle.model || ""}`.trim();

            option.textContent = vehicleName || "Vehicle";

            vehicleSelect.appendChild(option);

        });

    } catch (error) {

        vehicleSelect.innerHTML =
            '<option value="">Unable to load vehicles</option>';

        console.error(error);

    }

}


// Submit a new review
async function submitReview(event) {

    event.preventDefault();

    const vehicleId = document.getElementById("vehicleId").value;
    const rating = document.getElementById("rating").value;
    const comment = document.getElementById("comment").value.trim();

    const message = document.getElementById("reviewMessage");
    const submitButton = document.querySelector(
        '#reviewForm button[type="submit"]'
    );

    message.innerHTML = "";

    if (!vehicleId || !rating || !comment) {

        message.innerHTML = `
            <div class="alert alert-warning">
                Please select a vehicle, rating and enter a comment.
            </div>
        `;

        return;
    }

    try {

        submitButton.disabled = true;
        submitButton.textContent = "Submitting...";

        const response = await fetch(
            `/api/reviews?vehicleId=${vehicleId}`,
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    rating: Number(rating),
                    comment: comment
                })
            }
        );

        if (!response.ok) {

            let errorMessage = "Unable to submit your review.";

            try {
                const errorData = await response.json();
                errorMessage = errorData.message || errorMessage;
            } catch (e) {
                // Keep the default error message
            }

            throw new Error(errorMessage);

        }

        message.innerHTML = `
            <div class="alert alert-success">
                Your review has been submitted successfully!
            </div>
        `;

        document.getElementById("reviewForm").reset();

        await loadMyReviews();

    } catch (error) {

        message.innerHTML = `
            <div class="alert alert-danger">
                ${escapeHtml(error.message)}
            </div>
        `;

    } finally {

        submitButton.disabled = false;
        submitButton.textContent = "Submit Review";

    }

}


// Load reviews submitted by the logged-in customer
async function loadMyReviews() {

    const container = document.getElementById("reviewsContainer");

    try {

        // Get logged-in customer details
        const userResponse = await fetch("/api/auth/me");

        if (!userResponse.ok) {
            window.location.href = "login.html";
            return;
        }

        const user = await userResponse.json();

        if (user.role !== "CUSTOMER") {
            window.location.href = "login.html";
            return;
        }

        // Load reviews submitted by this customer
        const response = await fetch(`/api/reviews/user/${user.id}`);

        if (!response.ok) {
            throw new Error("Unable to load your reviews.");
        }

        const reviews = await response.json();

        if (!reviews || reviews.length === 0) {

            container.innerHTML = `
                <div class="review-card text-center py-5">
                    <h5>No Reviews Yet</h5>
                    <p class="text-muted mb-0">
                        You have not submitted any vehicle reviews yet.
                    </p>
                </div>
            `;

            return;
        }

        container.innerHTML = reviews.map(review => `

            <div class="review-card">

                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">

                    <div>

                        <div class="vehicle-name">
                            ${escapeHtml(review.vehicleName || "Vehicle")}
                        </div>

                        <div class="stars">
                            ${"★".repeat(review.rating || 0)}${"☆".repeat(5 - (review.rating || 0))}
                        </div>

                        <p class="mt-3 mb-2">
                            ${escapeHtml(review.comment || "No comment provided.")}
                        </p>

                        <div class="review-date">
                            Reviewed on: ${formatReviewDate(review.createdAt)}
                        </div>

                    </div>

                    <button class="btn btn-outline-danger btn-sm"
                            onclick="deleteReview(${review.id})">
                        Delete
                    </button>

                </div>

            </div>

        `).join("");

    } catch (error) {

        container.innerHTML = `
            <div class="alert alert-danger">
                ${escapeHtml(error.message)}
            </div>
        `;

    }

}


// Delete a review
async function deleteReview(reviewId) {

    if (!confirm("Are you sure you want to delete this review?")) {
        return;
    }

    try {

        const response = await fetch(`/api/reviews/${reviewId}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            throw new Error("Unable to delete review.");
        }

        alert("Review deleted successfully!");

        loadMyReviews();

    } catch (error) {

        alert(error.message);

    }

}


// Format review date
function formatReviewDate(date) {

    if (!date) return "Date unavailable";

    return new Date(date).toLocaleDateString("en-IN", {
        day: "2-digit",
        month: "short",
        year: "numeric"
    });

}


// Escape HTML to prevent unsafe content
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
