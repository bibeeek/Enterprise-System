// ─────────────────────────────────────────────
// AUTH CHECK — use role-specific token key
// ─────────────────────────────────────────────
const token = localStorage.getItem("jwtToken_ROLE_USER");
if (!token) window.location.href = "/login.html";

// ─────────────────────────────────────────────
// HELPERS
// ─────────────────────────────────────────────
function authHeaders() {
    return {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + token
    };
}

function showMsg(id, text, type) {
    const el = document.getElementById(id);
    el.textContent = text;
    el.className = "msg msg-" + type + " show";
    setTimeout(() => el.className = "msg", 3000);
}

function badgeClass(status) {
    const map = {
        "OPEN":        "badge-todo",
        "IN_PROGRESS": "badge-inprogress",
        "DONE":        "badge-done",
        "CANCELLED":   "badge-cancelled",
        "OVERDUE":     "badge-overdue"
    };
    return map[status] || "badge-todo";
}

// ─────────────────────────────────────────────
// LOAD USER DETAILS
// GET /user/viewUserDetails
// ─────────────────────────────────────────────
async function loadUserDetails() {
    try {
        const response = await fetch("http://localhost:8080/user/viewUserDetails", {
            method: "GET",
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        if (!response.ok) throw new Error("Failed to load user details");

        const result = await response.json();
        const user   = result.data;

        document.getElementById("u-userName").textContent = user.userName     || "—";
        document.getElementById("u-fullName").textContent = user.fullName     || "—";
        document.getElementById("u-email").textContent    = user.email        || "—";
        document.getElementById("u-phone").textContent    = user.phoneNumber  || "—";
        document.getElementById("u-address").textContent  = user.address      || "—";
        document.getElementById("u-role").textContent     = user.role         || "—";

        // ── department — show badge, blue if assigned, red if not ──
        // your UserResponseDto field is "DepartmentName" with capital D
        const deptEl = document.getElementById("u-department");
        if (user.DepartmentName) {
            deptEl.innerHTML = `<span class="dept-badge dept-assigned">📁 ${user.DepartmentName}</span>`;
        } else {
            deptEl.innerHTML = `<span class="dept-badge dept-unassigned">Not assigned</span>`;
        }

        // pre-fill edit form
        document.getElementById("edit-fullName").value = user.fullName    || "";
        document.getElementById("edit-phone").value    = user.phoneNumber || "";
        document.getElementById("edit-address").value  = user.address     || "";

        document.getElementById("userLoading").style.display  = "none";
        document.getElementById("userDetails").style.display  = "block";

    } catch (error) {
        console.error(error);
        document.getElementById("userLoading").style.display = "none";
        showMsg("userMsg", "Could not load profile.", "error");
    }
}

// ─────────────────────────────────────────────
// TOGGLE EDIT FORM
// ─────────────────────────────────────────────
function toggleEditForm() {
    document.getElementById("editForm").classList.toggle("open");
}

// ─────────────────────────────────────────────
// UPDATE USER DETAILS
// PATCH /user/updateUserDetails
// ─────────────────────────────────────────────
async function updateUserDetails() {
    const updateData = {
        fullName:    document.getElementById("edit-fullName").value,
        phoneNumber: document.getElementById("edit-phone").value,
        address:     document.getElementById("edit-address").value
    };

    try {
        const response = await fetch("http://localhost:8080/user/updateUserDetails", {
            method: "PATCH",
            headers: authHeaders(),
            body: JSON.stringify(updateData)
        });

        if (response.status === 401) { logout(); return; }
        if (!response.ok) throw new Error("Update failed");

        showMsg("userMsg", "Profile updated successfully!", "success");
        toggleEditForm();
        loadUserDetails();

    } catch (error) {
        console.error(error);
        showMsg("userMsg", "Could not update profile.", "error");
    }
}

// ─────────────────────────────────────────────
// LOAD TASKS
// GET /user/viewAllTasks
// ─────────────────────────────────────────────
async function loadTasks() {
    try {
        const response = await fetch("http://localhost:8080/user/viewAllTasks", {
            method: "GET",
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }

        document.getElementById("taskLoading").style.display = "none";

        if (!response.ok) {
            const err = await response.json();
            document.getElementById("taskEmpty").style.display = "block";
            document.getElementById("taskEmpty").textContent = err.message || "No tasks found.";
            return;
        }

        const result = await response.json();
        const tasks  = result.data;

        if (!tasks || tasks.length === 0) {
            document.getElementById("taskEmpty").style.display = "block";
            return;
        }

        const tbody = document.getElementById("taskTableBody");
        tbody.innerHTML = "";

        tasks.forEach(task => {
            const isDone      = task.taskStatus === "DONE";
            const isCancelled = task.taskStatus === "CANCELLED";
            const canComplete = !isDone && !isCancelled;

            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${task.taskName           || "—"}</td>
                <td>${task.taskDescription    || "—"}</td>
                <td>${task.taskPriority       || "—"}</td>
                <td>${task.taskCategory       || "—"}</td>
                <td>${task.deadline           || "—"}</td>
                <td>${task.estimatedTimeInHrs || "—"} hrs</td>
                <td>${task.departmentName     || "—"}</td>
                <td>${task.assignedTo         || "—"}</td>
                <td><span class="badge ${badgeClass(task.taskStatus)}">${task.taskStatus}</span></td>
                <td>
                    ${canComplete
                ? `<button class="btn btn-success btn-sm" onclick="markAsCompleted('${task.taskName}')">Mark Done</button>`
                : `<span style="color:#aaa;font-size:12px;">${isDone ? "Completed" : "Cancelled"}</span>`
            }
                </td>`;
            tbody.appendChild(row);
        });

        document.getElementById("taskContainer").style.display = "block";

    } catch (error) {
        console.error(error);
        document.getElementById("taskLoading").style.display = "none";
        showMsg("taskMsg", "Could not load tasks.", "error");
    }
}

// ─────────────────────────────────────────────
// MARK TASK AS COMPLETED
// PUT /user/updateTaskStatusAsCompleted/{taskName}
// ─────────────────────────────────────────────
async function markAsCompleted(taskName) {
    if (!confirm(`Mark "${taskName}" as completed?`)) return;

    try {
        const response = await fetch(`http://localhost:8080/user/updateTaskStatusAsCompleted/${taskName}`, {
            method: "PUT",
            headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }

        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || "Failed to update task");
        }

        showMsg("taskMsg", `Task "${taskName}" marked as completed!`, "success");
        loadTasks();

    } catch (error) {
        console.error(error);
        showMsg("taskMsg", error.message || "Could not update task.", "error");
    }
}

// ─────────────────────────────────────────────
// LOGOUT
// ─────────────────────────────────────────────
function logout() {
    localStorage.removeItem("jwtToken_ROLE_USER");
    window.location.href = "/login.html";
}

// ─────────────────────────────────────────────
// INIT
// ─────────────────────────────────────────────
loadUserDetails();
loadTasks();