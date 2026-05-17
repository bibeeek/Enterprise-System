// ─────────────────────────────────────────────
// AUTH CHECK
// ─────────────────────────────────────────────
const token = localStorage.getItem("jwtToken_ROLE_ADMIN");
if (!token) window.location.href = "/login.html";

const PAGE_SIZE = 10;

// ─────────────────────────────────────────────
// PAGINATION STATE — tracks page + whether more data exists
// hasMore = false means Next button does nothing
// ─────────────────────────────────────────────
const state = {
    users:    { page: 0, hasMore: true },
    managers: { page: 0, hasMore: true },
    admins:   { page: 0, hasMore: true },
    audit:    { page: 0, hasMore: true }
};

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

// ── FIX: each section uses its own variable name ──
function roleBadge(role) {
    const map = {
        "ROLE_ADMIN":   "badge-admin",
        "ROLE_MANAGER": "badge-manager",
        "ROLE_USER":    "badge-user"
    };
    return `<span class="badge ${map[role] || 'badge-user'}">${role?.replace("ROLE_", "") || "—"}</span>`;
}

// ─────────────────────────────────────────────
// TAB SWITCHING
// ─────────────────────────────────────────────
function switchTab(name) {
    document.querySelectorAll(".tab").forEach(t => t.classList.remove("active"));
    document.querySelectorAll(".tab-content").forEach(t => t.classList.remove("active"));
    event.target.classList.add("active");
    document.getElementById("tab-" + name).classList.add("active");

    if (name === "users")       loadUsers();
    if (name === "managers")    loadManagers();
    if (name === "admins")      loadAdmins();
    if (name === "departments") loadDepartments();
    if (name === "audit")       loadAuditLogs();
}

// ─────────────────────────────────────────────
// PAGINATION
// only moves if there is more data (hasMore)
// ─────────────────────────────────────────────
function changePage(section, dir) {
    if (dir === -1 && state[section].page === 0) return;     // already on first page
    if (dir === 1  && !state[section].hasMore) return;       // no more data, stop

    state[section].page = Math.max(0, state[section].page + dir);
    document.getElementById(section + "Page").textContent = "Page " + (state[section].page + 1);

    if (section === "users")    loadUsers();
    if (section === "managers") loadManagers();
    if (section === "admins")   loadAdmins();
    if (section === "audit")    loadAuditLogs();
}

// after each fetch, call this to decide if Next should work
function updateHasMore(section, count) {
    state[section].hasMore = count === PAGE_SIZE;
}

// ─────────────────────────────────────────────
// LOAD USERS — GET /admin/getAllUsers
// ─────────────────────────────────────────────
async function loadUsers() {
    document.getElementById("usersLoading").style.display = "block";
    document.getElementById("usersContainer").style.display = "none";

    try {
        const response = await fetch(`http://localhost:8080/admin/getAllUsers?page=${state.users.page}&size=${PAGE_SIZE}`, {
            method: "GET", headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }

        const tbody = document.getElementById("usersBody");
        tbody.innerHTML = "";

        if (!response.ok) {
            // backend throws error when no records — treat as empty
            updateHasMore("users", 0);
            tbody.innerHTML = `<tr><td colspan="11" class="empty">No users found.</td></tr>`;
            document.getElementById("usersLoading").style.display = "none";
            document.getElementById("usersContainer").style.display = "block";
            return;
        }

        const result = await response.json();
        const users = result.data || [];
        updateHasMore("users", users.length);

        if (users.length === 0) {
            tbody.innerHTML = `<tr><td colspan="11" class="empty">No users found.</td></tr>`;
        } else {
            users.forEach((user, index) => {
                // ── correct variable: user.role not manager.role ──
                const actionBtn = user.isActive
                    ? `<button class="btn btn-danger btn-sm" onclick="disableUser('${user.userName}', ${index})">Disable</button>`
                    : `<button class="btn btn-success btn-sm" onclick="enableUser('${user.userName}', ${index})">Enable</button>`;

                tbody.innerHTML += `
                    <tr>
                        <td>${user.userName     || "—"}</td>
                        <td>${user.fullName      || "—"}</td>
                        <td>${user.email         || "—"}</td>
                        <td>${user.phoneNumber   || "—"}</td>
                        <td>${user.address       || "—"}</td>
                        <td>${user.departmentName || "Not assigned"}</td>
                        <td>${roleBadge(user.role)}</td>
                        <td id="status-u-${index}">
                            <span class="badge ${user.isActive ? 'badge-active' : 'badge-inactive'}">
                                ${user.isActive ? "Active" : "Inactive"}
                            </span>
                        </td>
                        <td>${user.lastLogin || "Never"}</td>
                        <td>${user.createdAt || "—"}</td>
                        <td id="action-u-${index}">${actionBtn}</td>
                    </tr>`;
            });
        }

        document.getElementById("usersLoading").style.display = "none";
        document.getElementById("usersContainer").style.display = "block";

    } catch (err) {
        console.error("loadUsers error:", err);
        document.getElementById("usersLoading").style.display = "none";
        showMsg("usersMsg", "Could not load users: " + err.message, "error");
    }
}

// ─────────────────────────────────────────────
// ENABLE / DISABLE USER — updates row in place, no reload
// ─────────────────────────────────────────────
async function enableUser(username, index) {
    try {
        const res = await fetch(`http://localhost:8080/admin/enableUser/${username}`, {
            method: "PUT", headers: authHeaders()
        });
        if (!res.ok) throw new Error((await res.json()).message || "Failed");

        document.getElementById(`status-u-${index}`).innerHTML = `<span class="badge badge-active">Active</span>`;
        document.getElementById(`action-u-${index}`).innerHTML = `<button class="btn btn-danger btn-sm" onclick="disableUser('${username}', ${index})">Disable</button>`;
        showMsg("usersMsg", `${username} enabled.`, "success");
    } catch (err) {
        showMsg("usersMsg", err.message || `Could not enable ${username}.`, "error");
    }
}

async function disableUser(username, index) {
    if (!confirm(`Disable user "${username}"?`)) return;
    try {
        const res = await fetch(`http://localhost:8080/admin/disableUser/${username}`, {
            method: "PUT", headers: authHeaders()
        });
        if (!res.ok) throw new Error((await res.json()).message || "Failed");

        document.getElementById(`status-u-${index}`).innerHTML = `<span class="badge badge-inactive">Inactive</span>`;
        document.getElementById(`action-u-${index}`).innerHTML = `<button class="btn btn-success btn-sm" onclick="enableUser('${username}', ${index})">Enable</button>`;
        showMsg("usersMsg", `${username} disabled.`, "success");
    } catch (err) {
        showMsg("usersMsg", err.message || `Could not disable ${username}.`, "error");
    }
}

// ─────────────────────────────────────────────
// LOAD MANAGERS — GET /admin/getAllManagers
// ─────────────────────────────────────────────
async function loadManagers() {
    document.getElementById("managersLoading").style.display = "block";
    document.getElementById("managersContainer").style.display = "none";

    try {
        const response = await fetch(`http://localhost:8080/admin/getAllManagers?page=${state.managers.page}&size=${PAGE_SIZE}`, {
            method: "GET", headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }

        const tbody = document.getElementById("managersBody");
        tbody.innerHTML = "";

        if (!response.ok) {
            updateHasMore("managers", 0);
            tbody.innerHTML = `<tr><td colspan="11" class="empty">No managers found.</td></tr>`;
            document.getElementById("managersLoading").style.display = "none";
            document.getElementById("managersContainer").style.display = "block";
            return;
        }

        const result = await response.json();
        const managers = result.data || [];
        updateHasMore("managers", managers.length);

        if (managers.length === 0) {
            tbody.innerHTML = `<tr><td colspan="11" class="empty">No managers found.</td></tr>`;
        } else {
            managers.forEach((m, index) => {
                // ── FIX: m.role not user.role ──
                const actionBtn = m.isActive
                    ? `<button class="btn btn-danger btn-sm" onclick="disableManager('${m.userName}', ${index})">Disable</button>`
                    : `<button class="btn btn-success btn-sm" onclick="enableManager('${m.userName}', ${index})">Enable</button>`;

                tbody.innerHTML += `
                    <tr>
                        <td>${m.userName      || "—"}</td>
                        <td>${m.fullName       || "—"}</td>
                        <td>${m.email          || "—"}</td>
                        <td>${m.phoneNumber    || "—"}</td>
                        <td>${m.address        || "—"}</td>
                        <td>${m.departmentName || "Not assigned"}</td>
                        <td>${roleBadge(m.role)}</td>
                        <td id="status-m-${index}">
                            <span class="badge ${m.isActive ? 'badge-active' : 'badge-inactive'}">
                                ${m.isActive ? "Active" : "Inactive"}
                            </span>
                        </td>
                        <td>${m.lastLogin || "Never"}</td>
                        <td>${m.createdAt || "—"}</td>
                        <td id="action-m-${index}">${actionBtn}</td>
                    </tr>`;
            });
        }

        document.getElementById("managersLoading").style.display = "none";
        document.getElementById("managersContainer").style.display = "block";

    } catch (err) {
        console.error("loadManagers error:", err);
        document.getElementById("managersLoading").style.display = "none";
        showMsg("managersMsg", "Could not load managers: " + err.message, "error");
    }
}

// ─────────────────────────────────────────────
// ENABLE / DISABLE MANAGER — updates row in place
// ─────────────────────────────────────────────
async function enableManager(username, index) {
    try {
        const res = await fetch(`http://localhost:8080/admin/enableManager/${username}`, {
            method: "PUT", headers: authHeaders()
        });
        if (!res.ok) throw new Error((await res.json()).message || "Failed");

        document.getElementById(`status-m-${index}`).innerHTML = `<span class="badge badge-active">Active</span>`;
        document.getElementById(`action-m-${index}`).innerHTML = `<button class="btn btn-danger btn-sm" onclick="disableManager('${username}', ${index})">Disable</button>`;
        showMsg("managersMsg", `${username} enabled.`, "success");
    } catch (err) {
        showMsg("managersMsg", err.message || `Could not enable ${username}.`, "error");
    }
}

async function disableManager(username, index) {
    if (!confirm(`Disable manager "${username}"?`)) return;
    try {
        const res = await fetch(`http://localhost:8080/admin/disableManager/${username}`, {
            method: "PUT", headers: authHeaders()
        });
        if (!res.ok) throw new Error((await res.json()).message || "Failed");

        document.getElementById(`status-m-${index}`).innerHTML = `<span class="badge badge-inactive">Inactive</span>`;
        document.getElementById(`action-m-${index}`).innerHTML = `<button class="btn btn-success btn-sm" onclick="enableManager('${username}', ${index})">Enable</button>`;
        showMsg("managersMsg", `${username} disabled.`, "success");
    } catch (err) {
        showMsg("managersMsg", err.message || `Could not disable ${username}.`, "error");
    }
}

// ─────────────────────────────────────────────
// LOAD ADMINS — GET /admin/getAllAdmins
// ─────────────────────────────────────────────
async function loadAdmins() {
    document.getElementById("adminsLoading").style.display = "block";
    document.getElementById("adminsContainer").style.display = "none";

    try {
        const response = await fetch(`http://localhost:8080/admin/getAllAdmins?page=${state.admins.page}&size=${PAGE_SIZE}`, {
            method: "GET", headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }

        const tbody = document.getElementById("adminsBody");
        tbody.innerHTML = "";

        if (!response.ok) {
            updateHasMore("admins", 0);
            tbody.innerHTML = `<tr><td colspan="10" class="empty">No admins found.</td></tr>`;
            document.getElementById("adminsLoading").style.display = "none";
            document.getElementById("adminsContainer").style.display = "block";
            return;
        }

        const result = await response.json();
        const admins = result.data || [];
        updateHasMore("admins", admins.length);

        if (admins.length === 0) {
            tbody.innerHTML = `<tr><td colspan="10" class="empty">No admins found.</td></tr>`;
        } else {
            admins.forEach((a, index) => {
                // ── FIX: a.role not user.role ──
                tbody.innerHTML += `
                    <tr>
                        <td>${a.userName      || "—"}</td>
                        <td>${a.fullName       || "—"}</td>
                        <td>${a.email          || "—"}</td>
                        <td>${a.phoneNumber    || "—"}</td>
                        <td>${a.address        || "—"}</td>
                        <td>${a.departmentName || "Not assigned"}</td>
                        <td>${roleBadge(a.role)}</td>
                        <td>${a.lastLogin || "Never"}</td>
                        <td>${a.createdAt || "—"}</td>
                    </tr>`;
            });
        }

        document.getElementById("adminsLoading").style.display = "none";
        document.getElementById("adminsContainer").style.display = "block";

    } catch (err) {
        console.error("loadAdmins error:", err);
        document.getElementById("adminsLoading").style.display = "none";
        showMsg("adminsMsg", "Could not load admins: " + err.message, "error");
    }
}

// ─────────────────────────────────────────────
// LOAD DEPARTMENTS — GET /admin/viewAllDepartments
// ─────────────────────────────────────────────
async function loadDepartments() {
    document.getElementById("deptsLoading").style.display = "block";
    document.getElementById("deptsContainer").style.display = "none";

    try {
        const response = await fetch(`http://localhost:8080/admin/viewAllDepartments`, {
            method: "GET", headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }

        const tbody = document.getElementById("deptsBody");
        tbody.innerHTML = "";

        if (!response.ok) {
            tbody.innerHTML = `<tr><td colspan="4" class="empty">No departments found.</td></tr>`;
            document.getElementById("deptsLoading").style.display = "none";
            document.getElementById("deptsContainer").style.display = "block";
            return;
        }

        const result = await response.json();
        const depts = result.data || [];

        if (depts.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" class="empty">No departments found.</td></tr>`;
        } else {
            depts.forEach((d,index) => {
                tbody.innerHTML += `
                    <tr>
                        <td>${d.departmentName        || "—"}</td>
            <td>${d.departmentDescription || "—"}</td>
            <td>${d.managersUserNames && d.managersUserNames.length > 0
                    ? d.managersUserNames.join(", ")
                    : "Not assigned"}</td>
            <td id="status-d-${index}">
                <span class="badge ${d.isActive ? 'badge-active' : 'badge-inactive'}">
                    ${d.isActive ? "Active" : "Inactive"}
                </span>
            </td>
            <td>
                <button class="btn btn-warning btn-sm" onclick="openAssignModal('${d.departmentName}')">Assign Manager</button>
                ${d.isActive
                    ? `<button class="btn btn-danger btn-sm" style="margin-left:5px;" onclick="disableDepartment('${d.departmentName}', ${index})">Disable</button>`
                    : `<button class="btn btn-success btn-sm" style="margin-left:5px;" onclick="enableDepartment('${d.departmentName}', ${index})">Enable</button>`
                }
            </td>
                    </tr>`;
            });
        }

        document.getElementById("deptsLoading").style.display = "none";
        document.getElementById("deptsContainer").style.display = "block";

    } catch (err) {
        console.error("loadDepartments error:", err);
        document.getElementById("deptsLoading").style.display = "none";
        showMsg("deptsMsg", "Could not load departments: " + err.message, "error");
    }
}
async function enableDepartment(departmentName, index) {
    try {
        const res = await fetch(`http://localhost:8080/admin/enableDepartment/${departmentName}`, {
            method: "PUT", headers: authHeaders()
        });
        if (!res.ok) throw new Error((await res.json()).message || "Failed");

        document.getElementById(`status-d-${index}`).innerHTML = `<span class="badge badge-active">Active</span>`;
        showMsg("deptsMsg", `${departmentName} enabled.`, "success");
        loadDepartments();
    } catch (err) {
        showMsg("deptsMsg", err.message || "Could not enable department.", "error");
    }
}

async function disableDepartment(departmentName, index) {
    if (!confirm(`Disable department "${departmentName}"?`)) return;
    try {
        const res = await fetch(`http://localhost:8080/admin/disableDepartment/${departmentName}`, {
            method: "PUT", headers: authHeaders()
        });
        if (!res.ok) throw new Error((await res.json()).message || "Failed");

        document.getElementById(`status-d-${index}`).innerHTML = `<span class="badge badge-inactive">Inactive</span>`;
        showMsg("deptsMsg", `${departmentName} disabled.`, "success");
        loadDepartments();
    } catch (err) {
        showMsg("deptsMsg", err.message || "Could not disable department.", "error");
    }
}

// ─────────────────────────────────────────────
// ADD DEPARTMENT
// ─────────────────────────────────────────────
async function addDepartment() {
    const name = document.getElementById("deptName").value.trim();
    const desc = document.getElementById("deptDesc").value.trim();

    if (!name) return showMsg("addDeptMsg", "Department name is required.", "error");
    if (!desc) return showMsg("addDeptMsg", "Department description is required.", "error");

    try {
        const response = await fetch("http://localhost:8080/admin/addDepartment", {
            method: "POST",
            headers: authHeaders(),
            body: JSON.stringify({ departmentName: name, departmentDescription: desc })
        });

        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || JSON.stringify(err));
        }

        showMsg("addDeptMsg", "Department added successfully!", "success");
        document.getElementById("deptName").value = "";
        document.getElementById("deptDesc").value = "";
        loadDepartments();

    } catch (err) {
        showMsg("addDeptMsg", err.message || "Could not add department.", "error");
    }
}

// ─────────────────────────────────────────────
// ASSIGN MANAGER MODAL
// ─────────────────────────────────────────────
function openAssignModal(deptName) {
    document.getElementById("assign-dept").value = deptName;
    document.getElementById("assign-manager").value = "";
    document.getElementById("assignModal").style.display = "flex";
}

function closeAssignModal() {
    document.getElementById("assignModal").style.display = "none";
}

async function assignManager() {
    const dept    = document.getElementById("assign-dept").value;
    const manager = document.getElementById("assign-manager").value.trim();

    if (!manager) return showMsg("assignMsg", "Enter a manager username.", "error");

    try {
        const response = await fetch(`http://localhost:8080/admin/assignManagersToDepartment/${manager}/${dept}`, {
            method: "PUT", headers: authHeaders()
        });

        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || "Failed");
        }

        showMsg("deptsMsg", `Manager "${manager}" assigned to "${dept}".`, "success");
        closeAssignModal();
        loadDepartments();

    } catch (err) {
        showMsg("assignMsg", err.message || "Could not assign manager.", "error");
    }
}

// ─────────────────────────────────────────────
// ADD ADMIN
// ─────────────────────────────────────────────
async function addAdmin() {
    const data = {
        userName:    document.getElementById("a-userName").value.trim(),
        fullName:    document.getElementById("a-fullName").value.trim(),
        email:       document.getElementById("a-email").value.trim(),
        passWord:    document.getElementById("a-password").value,
        phoneNumber: document.getElementById("a-phone").value.trim(),
        address:     document.getElementById("a-address").value.trim()
    };

    if (!data.userName || !data.email || !data.passWord) {
        return showMsg("addAdminMsg", "Username, email and password are required.", "error");
    }

    try {
        const response = await fetch("http://localhost:8080/admin/addAdmin", {
            method: "POST", headers: authHeaders(), body: JSON.stringify(data)
        });

        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || "Failed");
        }

        showMsg("addAdminMsg", "Admin added successfully!", "success");
        ["a-userName","a-fullName","a-email","a-password","a-phone","a-address"]
            .forEach(id => document.getElementById(id).value = "");

    } catch (err) {
        showMsg("addAdminMsg", err.message || "Could not add admin.", "error");
    }
}

// ─────────────────────────────────────────────
// ADD MANAGER
// ─────────────────────────────────────────────
async function addManager() {
    const data = {
        userName:    document.getElementById("m-userName").value.trim(),
        fullName:    document.getElementById("m-fullName").value.trim(),
        email:       document.getElementById("m-email").value.trim(),
        passWord:    document.getElementById("m-password").value,
        phoneNumber: document.getElementById("m-phone").value.trim(),
        address:     document.getElementById("m-address").value.trim()
    };

    if (!data.userName || !data.email || !data.passWord) {
        return showMsg("addManagerMsg", "Username, email and password are required.", "error");
    }

    try {
        const response = await fetch("http://localhost:8080/admin/addManager", {
            method: "POST", headers: authHeaders(), body: JSON.stringify(data)
        });

        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || "Failed");
        }

        showMsg("addManagerMsg", "Manager added successfully!", "success");
        ["m-userName","m-fullName","m-email","m-password","m-phone","m-address"]
            .forEach(id => document.getElementById(id).value = "");

    } catch (err) {
        showMsg("addManagerMsg", err.message || "Could not add manager.", "error");
    }
}

// ─────────────────────────────────────────────
// LOAD AUDIT LOGS
// ─────────────────────────────────────────────
// call with resetPage=true when filter button clicked
function filterAudit() {
    state.audit.page = 0;
    state.audit.hasMore = true;
    document.getElementById("auditPage").textContent = "Page 1";
    loadAuditLogs();
}

async function loadAuditLogs() {
    document.getElementById("auditLoading").style.display = "block";
    document.getElementById("auditContainer").style.display = "none";

    const startDate = document.getElementById("auditStart").value;
    const endDate   = document.getElementById("auditEnd").value;
    const sortBy    = document.getElementById("auditSortBy").value;
    const order     = document.getElementById("auditOrder").value;

    let url = `http://localhost:8080/admin/viewAllAuditLogs?page=${state.audit.page}&size=${PAGE_SIZE}&order=${order}&sortBy=${sortBy}`;
    if (startDate) url += `&startDate=${startDate}`;
    if (endDate)   url += `&endDate=${endDate}`;

    try {
        const response = await fetch(url, {
            method: "GET", headers: authHeaders()
        });

        if (response.status === 401) { logout(); return; }

        const tbody = document.getElementById("auditBody");
        tbody.innerHTML = "";

        if (!response.ok) {
            updateHasMore("audit", 0);
            tbody.innerHTML = `<tr><td colspan="4" class="empty">No audit logs found.</td></tr>`;
            document.getElementById("auditLoading").style.display = "none";
            document.getElementById("auditContainer").style.display = "block";
            return;
        }

        const result = await response.json();
        const logs = result.data || [];
        updateHasMore("audit", logs.length); // if < PAGE_SIZE, Next button disabled

        if (logs.length === 0) {
            tbody.innerHTML = `<tr><td colspan="4" class="empty">No audit logs found.</td></tr>`;
        } else {
            logs.forEach(log => {
                tbody.innerHTML += `
                    <tr>
                        <td>${log.action       || "—"}</td>
                        <td>${log.performedBy  || "—"}</td>
                        <td>${log.targetEntity || "—"}</td>
                        <td>${log.timestamp ? new Date(log.timestamp).toLocaleString() : "—"}</td>
                    </tr>`;
            });
        }

        document.getElementById("auditLoading").style.display = "none";
        document.getElementById("auditContainer").style.display = "block";

    } catch (err) {
        console.error("loadAuditLogs error:", err);
        document.getElementById("auditLoading").style.display = "none";
        showMsg("auditMsg", "Could not load audit logs: " + err.message, "error");
    }
}

// ─────────────────────────────────────────────
// LOGOUT
// ─────────────────────────────────────────────
function logout() {
    localStorage.removeItem("jwtToken_ROLE_ADMIN");
    window.location.href = "/login.html";
}

// ─────────────────────────────────────────────
// INIT
// ─────────────────────────────────────────────
loadUsers();