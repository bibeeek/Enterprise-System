// ─────────────────────────────────────────────
// AUTH CHECK
// ─────────────────────────────────────────────
const token = localStorage.getItem("jwtToken_ROLE_MANAGER");

if (!token) window.location.href = "/login.html";

let myDepartment = null;

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
    if (!el) return;
    el.textContent = text;
    el.className = "msg msg-" + type + " show";
    setTimeout(() => el.className = "msg", 4000);
}

function statusBadge(status) {
    const map = {
        "OPEN":        "badge-open",
        "IN_PROGRESS": "badge-inprogress",
        "DONE":        "badge-done",
        "CANCELLED":   "badge-cancelled",
        "OVERDUE":     "badge-overdue"
    };
    return `<span class="badge ${map[status] || 'badge-open'}">${status || "—"}</span>`;
}

// safe json parse — won't crash on empty body
async function safeJson(response) {
    try { return await response.json(); } catch { return {}; }
}

// ─────────────────────────────────────────────
// TAB SWITCHING
// ─────────────────────────────────────────────
function switchTab(name) {
    document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));
    document.querySelectorAll(".tab-content").forEach(t => t.classList.remove("active"));
    event.target.classList.add("active");
    document.getElementById("tab-" + name).classList.add("active");

    if (name === "tasks")      loadTasks();
    if (name === "users")      loadDeptUsers();
    if (name === "managers")   loadDeptManagers();
    if (name === "assignUser") loadUnassignedUsers();
}

function openModal(id) { document.getElementById(id).classList.add("open"); }
function closeModal(id) { document.getElementById(id).classList.remove("open"); }

// ─────────────────────────────────────────────
// LOAD MANAGER INFO — runs on page load
// GET /manager/getMyDepartment
// ─────────────────────────────────────────────
async function loadManagerInfo() {
    try {
        const response = await fetch("http://localhost:8080/manager/getMyDepartment", {
            method: "GET", headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }

        if (!response.ok) {
            const err = await safeJson(response);
            throw new Error(err.message || "Could not load department");
        }

        const result = await response.json();
        myDepartment = result.data.departmentName;
        document.getElementById("deptLabel").textContent = "📁 " + myDepartment;

    } catch (err) {
        console.error("loadManagerInfo error:", err);
        document.getElementById("deptLabel").textContent = "No department assigned";
    }
}

// ─────────────────────────────────────────────
// LOAD TASKS
// GET /manager/getAllTasksAssignedToTheDepartment
// ─────────────────────────────────────────────
async function loadTasks() {
    document.getElementById("tasksLoading").style.display = "block";
    document.getElementById("tasksContainer").style.display = "none";
    document.getElementById("tasksEmpty").style.display = "none";

    try {
        const response = await fetch("http://localhost:8080/manager/getAllTasksAssignedToTheDepartment", {
            method: "GET", headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        document.getElementById("tasksLoading").style.display = "none";

        if (!response.ok) {
            const err = await safeJson(response);
            document.getElementById("tasksEmpty").style.display = "block";
            document.getElementById("tasksEmpty").textContent = err.message || "No tasks found.";
            return;
        }

        const result = await response.json();
        const tasks = result.data || [];

        if (tasks.length === 0) {
            document.getElementById("tasksEmpty").style.display = "block";
            return;
        }

        const tbody = document.getElementById("tasksBody");
        tbody.innerHTML = "";

        tasks.forEach((task, index) => {
            const isDone      = task.taskStatus === "DONE";
            const isCancelled = task.taskStatus === "CANCELLED";
            const isFinished  = isDone || isCancelled;
            const isAssigned  = task.assignedTo && task.assignedTo !== "" && task.assignedTo !== "Unassigned";

            tbody.innerHTML += `
                <tr>
                    <td>${task.taskName        || "—"}</td>
                    <td>${task.taskDescription || "—"}</td>
                    <td>${task.taskPriority    || "—"}</td>
                    <td>${task.taskCategory    || "—"}</td>
                    <td>${statusBadge(task.taskStatus)}</td>
                    <td>${task.assignedTo      || "Unassigned"}</td>
                    <td>${task.deadline        || "—"}</td>
                    <td>${task.estimatedTimeInHrs || "—"} hrs</td>
                    <td id="active-t-${index}">
                        <span class="badge ${task.isActive ? 'badge-active' : 'badge-inactive'}">
                            ${task.isActive ? "Yes" : "No"}
                        </span>
                    </td>
                    <td>
                        <div style="display:flex; gap:5px; flex-wrap:wrap;">
                            ${isFinished
                // DONE or CANCELLED — no action buttons at all
                ? `<span style="color:#aaa;font-size:12px;">${isDone ? "✔ Completed" : "✖ Cancelled"}</span>`
                : `
                                    ${!isAssigned
                    // not assigned — show only Assign
                    ? `<button class="btn btn-info btn-sm" onclick="openAssignTaskModal('${task.taskName}', ${index})">Assign</button>`
                    // assigned — show Reassign + Cancel, hide Assign
                    : `
                                            <button class="btn btn-warning btn-sm" onclick="openReassignModal('${task.taskName}', '${task.assignedTo}')">Reassign</button>
                                            <button class="btn btn-danger btn-sm" onclick="openCancelModal('${task.taskName}', '${task.assignedTo}')">Cancel</button>
                                        `
                }
                                    ${task.isActive
                    ? `<button class="btn btn-danger btn-sm" id="active-btn-${index}" onclick="disableTask('${task.taskName}', ${index})">Disable</button>`
                    : `<button class="btn btn-success btn-sm" id="active-btn-${index}" onclick="enableTask('${task.taskName}', ${index})">Enable</button>`
                }
                                `
            }
                        </div>
                    </td>
                </tr>`;
        });

        document.getElementById("tasksContainer").style.display = "block";

    } catch (err) {
        console.error("loadTasks error:", err);
        document.getElementById("tasksLoading").style.display = "none";
        showMsg("tasksMsg", "Could not load tasks: " + err.message, "error");
    }
}

// ─────────────────────────────────────────────
// ADD TASK
// POST /manager/addNewTask
// ─────────────────────────────────────────────
async function addTask() {
    const name     = document.getElementById("t-name").value.trim();
    const desc     = document.getElementById("t-description").value.trim();
    const hours    = document.getElementById("t-hours").value;
    const priority = document.getElementById("t-priority").value;
    const category = document.getElementById("t-category").value;
    const status   = document.getElementById("t-status").value;
    const deadline = document.getElementById("t-deadline").value;

    if (!name || !desc || !hours || !deadline) {
        return showMsg("addTaskMsg", "Task name, description, estimated hours and deadline are required.", "error");
    }

    try {
        const response = await fetch("http://localhost:8080/manager/addNewTask", {
            method: "POST",
            headers: authHeaders(),
            body: JSON.stringify({
                taskName:           name,
                taskDescription:    desc,
                estimatedTimeInHrs: parseInt(hours),
                taskPriority:       priority || null,
                taskCategory:       category || null,
                taskStatus:         status   || null,
                deadline:           new Date(deadline).toISOString().slice(0, 19)
            })
        });

        if (!response.ok) {
            const err = await safeJson(response);
            throw new Error(err.message || "Failed to add task");
        }

        showMsg("addTaskMsg", "Task added successfully!", "success");
        ["t-name","t-description","t-hours","t-deadline"]
            .forEach(id => document.getElementById(id).value = "");
        document.getElementById("t-priority").value = "";
        document.getElementById("t-category").value = "";
        document.getElementById("t-status").value   = "OPEN";

    } catch (err) {
        showMsg("addTaskMsg", err.message || "Could not add task.", "error");
    }
}

// ─────────────────────────────────────────────
// ASSIGN TASK
// PUT /manager/assignTaskToEmployees/{userName}/{taskName}
// after assign: hide Assign button, show Reassign + Cancel
// ─────────────────────────────────────────────
function openAssignTaskModal(taskName, index) {
    document.getElementById("at-taskName").value = taskName;
    document.getElementById("at-userName").value = "";
    document.getElementById("at-taskName").dataset.index = index;
    openModal("assignTaskModal");
}

async function assignTask() {
    const taskName = document.getElementById("at-taskName").value;
    const userName = document.getElementById("at-userName").value.trim();
    const index    = document.getElementById("at-taskName").dataset.index;

    if (!userName) return showMsg("assignTaskMsg", "Enter an employee username.", "error");

    try {
        const response = await fetch(`http://localhost:8080/manager/assignTaskToEmployees/${userName}/${taskName}`, {
            method: "PUT", headers: authHeaders()
        });

        if (!response.ok) {
            const err = await safeJson(response);
            throw new Error(err.message || "Failed");
        }

        showMsg("tasksMsg", `Task "${taskName}" assigned to "${userName}".`, "success");
        closeModal("assignTaskModal");

        // ── swap Assign button → Reassign + Cancel without full reload ──
        const actionCell = document.querySelector(`#active-btn-${index}`)?.parentElement;
        if (actionCell) {
            const assignBtn = actionCell.querySelector(".btn-info");
            if (assignBtn) {
                assignBtn.outerHTML = `
                    <button class="btn btn-warning btn-sm" onclick="openReassignModal('${taskName}', '${userName}')">Reassign</button>
                    <button class="btn btn-danger btn-sm" onclick="openCancelModal('${taskName}', '${userName}')">Cancel</button>`;
            }
        }

    } catch (err) {
        showMsg("assignTaskMsg", err.message || "Could not assign task.", "error");
    }
}

// ─────────────────────────────────────────────
// REASSIGN TASK
// PUT /manager/reassignTaskToAnotherUser/{userName}/{taskName}/{targetUserName}
// ─────────────────────────────────────────────
function openReassignModal(taskName, currentUser) {
    document.getElementById("rt-taskName").value    = taskName;
    document.getElementById("rt-currentUser").value = currentUser;
    document.getElementById("rt-newUser").value     = "";
    openModal("reassignTaskModal");
}

async function reassignTask() {
    const taskName    = document.getElementById("rt-taskName").value;
    const currentUser = document.getElementById("rt-currentUser").value.trim();
    const newUser     = document.getElementById("rt-newUser").value.trim();

    if (!newUser) return showMsg("reassignTaskMsg", "Enter the new user's username.", "error");

    try {
        const response = await fetch(`http://localhost:8080/manager/reassignTaskToAnotherUser/${currentUser}/${taskName}/${newUser}`, {
            method: "PUT", headers: authHeaders()
        });

        if (!response.ok) {
            const err = await safeJson(response);
            throw new Error(err.message || "Failed");
        }

        showMsg("tasksMsg", `Task "${taskName}" reassigned to "${newUser}".`, "success");
        closeModal("reassignTaskModal");
        loadTasks();

    } catch (err) {
        showMsg("reassignTaskMsg", err.message || "Could not reassign task.", "error");
    }
}

// ─────────────────────────────────────────────
// CANCEL TASK
// PUT /manager/updateTaskStatusAsCancelled/{userName}/{taskName}
// ─────────────────────────────────────────────
function openCancelModal(taskName, assignedTo) {
    document.getElementById("ct-taskName").value = taskName;
    document.getElementById("ct-userName").value = assignedTo || "";
    openModal("cancelTaskModal");
}

async function cancelTask() {
    const taskName = document.getElementById("ct-taskName").value;
    const userName = document.getElementById("ct-userName").value.trim();

    try {
        const response = await fetch(`http://localhost:8080/manager/updateTaskStatusAsCancelled/${userName}/${taskName}`, {
            method: "PUT", headers: authHeaders()
        });

        if (!response.ok) {
            const err = await safeJson(response);
            throw new Error(err.message || "Failed");
        }

        showMsg("tasksMsg", `Task "${taskName}" cancelled.`, "success");
        closeModal("cancelTaskModal");
        loadTasks();

    } catch (err) {
        showMsg("cancelTaskMsg", err.message || "Could not cancel task.", "error");
    }
}

// ─────────────────────────────────────────────
// ENABLE / DISABLE TASK — updates row in place
// ─────────────────────────────────────────────
async function enableTask(taskName, index) {
    try {
        const res = await fetch(`http://localhost:8080/manager/enableTask/${taskName}`, {
            method: "PUT", headers: authHeaders()
        });
        if (!res.ok) throw new Error((await safeJson(res)).message || "Failed");

        document.getElementById(`active-t-${index}`).innerHTML = `<span class="badge badge-active">Yes</span>`;
        document.getElementById(`active-btn-${index}`).outerHTML =
            `<button class="btn btn-danger btn-sm" id="active-btn-${index}" onclick="disableTask('${taskName}', ${index})">Disable</button>`;
        showMsg("tasksMsg", `Task "${taskName}" enabled.`, "success");
    } catch (err) {
        showMsg("tasksMsg", err.message || "Could not enable task.", "error");
    }
}

async function disableTask(taskName, index) {
    if (!confirm(`Disable task "${taskName}"?`)) return;
    try {
        const res = await fetch(`http://localhost:8080/manager/disableTask/${taskName}`, {
            method: "PUT", headers: authHeaders()
        });
        if (!res.ok) throw new Error((await safeJson(res)).message || "Failed");

        document.getElementById(`active-t-${index}`).innerHTML = `<span class="badge badge-inactive">No</span>`;
        document.getElementById(`active-btn-${index}`).outerHTML =
            `<button class="btn btn-success btn-sm" id="active-btn-${index}" onclick="enableTask('${taskName}', ${index})">Enable</button>`;
        showMsg("tasksMsg", `Task "${taskName}" disabled.`, "success");
    } catch (err) {
        showMsg("tasksMsg", err.message || "Could not disable task.", "error");
    }
}

// ─────────────────────────────────────────────
// LOAD DEPARTMENT USERS
// GET /manager/getMyDepartmentUsers
// ─────────────────────────────────────────────
async function loadDeptUsers() {
    document.getElementById("usersLoading").style.display = "block";
    document.getElementById("usersContainer").style.display = "none";
    document.getElementById("usersEmpty").style.display = "none";

    try {
        const response = await fetch("http://localhost:8080/manager/getMyDepartmentUsers", {
            method: "GET", headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        document.getElementById("usersLoading").style.display = "none";

        if (!response.ok) {
            const err = await safeJson(response);
            document.getElementById("usersEmpty").style.display = "block";
            document.getElementById("usersEmpty").textContent = err.message || "No users found.";
            return;
        }

        const result = await response.json();
        const users  = result.data || [];

        if (users.length === 0) {
            document.getElementById("usersEmpty").style.display = "block";
            return;
        }

        const tbody = document.getElementById("usersBody");
        tbody.innerHTML = "";

        for (const [index, user] of users.entries()) {
            // fetch task count per user
            let taskCount = "—";
            try {
                const countRes = await fetch(`http://localhost:8080/manager/viewTaskCountOfUser/${user.userName}`, {
                    method: "GET", headers: authHeaders()
                });
                if (countRes.ok) {
                    const c = await countRes.json();
                    taskCount = c.data ?? "—";
                }
            } catch { /* ignore */ }

            tbody.innerHTML += `
                <tr>
                    <td>${user.userName    || "—"}</td>
                    <td>${user.fullName    || "—"}</td>
                    <td>${user.email       || "—"}</td>
                    <td>${user.phoneNumber || "—"}</td>
                    <td>${taskCount}</td>
                    <td id="unassign-btn-${index}">
                        <button class="btn btn-danger btn-sm" onclick="unassignUser('${user.userName}', ${index})">Unassign</button>
                    </td>
                </tr>`;
        }

        document.getElementById("usersContainer").style.display = "block";

    } catch (err) {
        console.error("loadDeptUsers error:", err);
        document.getElementById("usersLoading").style.display = "none";
        showMsg("usersMsg", "Could not load users: " + err.message, "error");
    }
}

// ─────────────────────────────────────────────
// UNASSIGN USER FROM DEPARTMENT
// PUT /manager/unassignUserFromDepartment/{username}
// ─────────────────────────────────────────────
async function unassignUser(userName, index) {
    if (!confirm(`Unassign "${userName}" from department?`)) return;
    try {
        const response = await fetch(`http://localhost:8080/manager/unassignUserFromDepartment/${userName}`, {
            method: "PUT", headers: authHeaders()
        });

        if (!response.ok) {
            const err = await safeJson(response);
            throw new Error(err.message || "Failed");
        }

        showMsg("usersMsg", `"${userName}" unassigned from department.`, "success");
        loadDeptUsers(); // reload to reflect change

    } catch (err) {
        showMsg("usersMsg", err.message || "Could not unassign user.", "error");
    }
}

// ─────────────────────────────────────────────
// LOAD DEPARTMENT MANAGERS
// GET /manager/getMyDepartmentManagers
// ─────────────────────────────────────────────
async function loadDeptManagers() {
    document.getElementById("managersLoading").style.display = "block";
    document.getElementById("managersContainer").style.display = "none";
    document.getElementById("managersEmpty").style.display = "none";

    try {
        const response = await fetch("http://localhost:8080/manager/getMyDepartmentManagers", {
            method: "GET", headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        document.getElementById("managersLoading").style.display = "none";

        if (!response.ok) {
            const err = await safeJson(response);
            document.getElementById("managersEmpty").style.display = "block";
            document.getElementById("managersEmpty").textContent = err.message || "No managers found.";
            return;
        }

        const result = await response.json();
        const managers = result.data || [];

        if (managers.length === 0) {
            document.getElementById("managersEmpty").style.display = "block";
            return;
        }

        const tbody = document.getElementById("managersBody");
        tbody.innerHTML = "";

        managers.forEach(m => {
            tbody.innerHTML += `
                <tr>
                    <td>${m.userName    || "—"}</td>
                    <td>${m.fullName    || "—"}</td>
                    <td>${m.email       || "—"}</td>
                    <td>${m.phoneNumber || "—"}</td>
                    <td><span class="badge ${m.isActive ? 'badge-active' : 'badge-inactive'}">${m.isActive ? "Active" : "Inactive"}</span></td>
                </tr>`;
        });

        document.getElementById("managersContainer").style.display = "block";

    } catch (err) {
        console.error("loadDeptManagers error:", err);
        document.getElementById("managersLoading").style.display = "none";
        showMsg("managersMsg", "Could not load managers: " + err.message, "error");
    }
}

// ─────────────────────────────────────────────
// LOAD UNASSIGNED USERS
// GET /manager/getAllUnassignedUsers
// shows all users with no department — one click to assign
// ─────────────────────────────────────────────
async function loadUnassignedUsers() {
    document.getElementById("unassignedLoading").style.display = "block";
    document.getElementById("unassignedContainer").style.display = "none";
    document.getElementById("unassignedEmpty").style.display = "none";

    try {
        const response = await fetch("http://localhost:8080/manager/getAllUnassignedUsers", {
            method: "GET", headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }
        document.getElementById("unassignedLoading").style.display = "none";

        if (!response.ok) {
            const err = await safeJson(response);
            document.getElementById("unassignedEmpty").style.display = "block";
            document.getElementById("unassignedEmpty").textContent = err.message || "No unassigned users.";
            return;
        }

        const result = await response.json();
        const users  = result.data || [];

        if (users.length === 0) {
            document.getElementById("unassignedEmpty").style.display = "block";
            return;
        }

        const tbody = document.getElementById("unassignedBody");
        tbody.innerHTML = "";

        users.forEach((user, index) => {
            tbody.innerHTML += `
                <tr id="unassigned-row-${index}">
                    <td>${user.userName    || "—"}</td>
                    <td>${user.fullName    || "—"}</td>
                    <td>${user.email       || "—"}</td>
                    <td>${user.phoneNumber || "—"}</td>
                    <td id="unassigned-action-${index}">
                        <!-- one click assign — no typing needed -->
                        <button class="btn btn-green btn-sm" onclick="assignUserToDept('${user.userName}', ${index})">
                            + Assign to My Dept
                        </button>
                    </td>
                </tr>`;
        });

        document.getElementById("unassignedContainer").style.display = "block";

    } catch (err) {
        console.error("loadUnassignedUsers error:", err);
        document.getElementById("unassignedLoading").style.display = "none";
        showMsg("unassignedMsg", "Could not load unassigned users: " + err.message, "error");
    }
}

// ─────────────────────────────────────────────
// ASSIGN USER TO DEPARTMENT
// POST /manager/assignUserToDepartment/{userName}/{departmentName}
// uses myDepartment — no manual input
// after assign: button flips to Unassign
// ─────────────────────────────────────────────
async function assignUserToDept(userName, index) {
    if (!myDepartment) {
        return showMsg("unassignedMsg", "Department info not loaded. Refresh the page.", "error");
    }

    try {
        const response = await fetch(`http://localhost:8080/manager/assignUserToDepartment/${userName}/${myDepartment}`, {
            method: "POST", headers: authHeaders()
        });

        if (!response.ok) {
            const err = await safeJson(response);
            throw new Error(err.message || "Failed");
        }

        showMsg("unassignedMsg", `"${userName}" assigned to "${myDepartment}".`, "success");

        // ── swap Assign button → Unassign button in place ──
        document.getElementById(`unassigned-action-${index}`).innerHTML =
            `<span style="color:#198754; font-weight:bold; font-size:12px;">✔ Assigned</span>`;

    } catch (err) {
        showMsg("unassignedMsg", err.message || "Could not assign user.", "error");
    }
}

// ─────────────────────────────────────────────
// LOGOUT
// ─────────────────────────────────────────────
function logout() {
    localStorage.removeItem("jwtToken_ROLE_MANAGER");
    window.location.href = "/login.html";
}

// ─────────────────────────────────────────────
// INIT
// ─────────────────────────────────────────────
loadManagerInfo();
loadTasks();