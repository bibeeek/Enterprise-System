document.addEventListener('DOMContentLoaded', function(){

    const loginForm = document.getElementById("loginForm");
    if (loginForm) {   // ← was if (!loginForm), that was backwards
        loginForm.addEventListener("submit", async function(event) {
            event.preventDefault();

            const loginData = {
                email: document.getElementById("email").value,
                passWord: document.getElementById("password").value
            };

            try {
                const response = await fetch("http://localhost:8080/auth/login", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(loginData)
                });

                if (!response.ok) {
                    alert("Invalid credentials");
                    return;
                }

                const data = await response.json();

                const role  = data.data.userResponseDto.role;
                const token = data.data.jwtToken;


                if (role === "ROLE_ADMIN") {
                    localStorage.setItem("jwtToken_ROLE_ADMIN", token);
                    window.location.href = "/adminhome.html";
                } else if (role === "ROLE_MANAGER") {
                    localStorage.setItem("jwtToken_ROLE_MANAGER", token);
                    window.location.href = "/managerhome.html";
                } else {
                    localStorage.setItem("jwtToken_ROLE_USER", token);
                    window.location.href = "/home.html";
                }


            } catch (error) {
                console.log(error);
                alert("Something went wrong");
            }
        });
    }

    const registerForm = document.getElementById("registerForm");
    if (registerForm) {
        registerForm.addEventListener("submit", async function(event) {
            event.preventDefault();

            const registerData = {
                userName:    document.getElementById("userName").value,
                fullName:    document.getElementById("fullName").value,
                email:       document.getElementById("email").value,
                passWord:    document.getElementById("password").value,
                address:     document.getElementById("address").value,
                phoneNumber: document.getElementById("phoneNumber").value
            };

            try {
                const response = await fetch("http://localhost:8080/auth/register", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(registerData)
                });

                if (!response.ok) {
                    const error = await response.text();
                    alert("Registration failed: " + error);
                    return;
                }

                const data = await response.json();
                alert("Registration successful!");
                window.location.href = "/login.html";

            } catch (error) {
                console.log(error);
                alert("Something went wrong");
            }
        });
    }

});