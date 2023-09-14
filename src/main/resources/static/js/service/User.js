export class User {
    constructor(token) {
        this.token = token;
        if (token) {
            this.headers = {
                ...this.headers,
                'Authorization': `Bearer ${token}`
            }
        } else {
            this.headers;
        }
    }

    async logout(url) {
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    ...this.headers,
                    'Content-Type': 'application/json'
                },
            });

            if (response.status === 200) {
                localStorage.removeItem('token');
                sessionStorage.removeItem('token');
                window.location.replace('/');
            } else {
                console.error(`failed to request on ${url} api ${response.statusText}`);
            }
        } catch (error) {
            console.error("error:", error);
        }
    }
    async updateUser(url, FormData) {
        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: {
                    ...this.headers,
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(FormData)
                ,
            });

            if (response.status === 200) {
                window.location.replace('/myPage');
            } else {
                console.error(`failed to request on ${url} api ${response.statusText}`);
            }
        } catch (error) {
            console.error("error:", error);
        }
    }
    async deleteUser(url) {
        try {
            const response = await fetch(url, {
                method: 'DELETE',
                headers: {
                    ...this.headers,
                    'Content-Type': 'application/json'
                },
            });

            if (response.status === 200) {
                window.location.replace('/');
            } else {
                console.error('Authentication failed: Token may be invalid or expired.');
                console.error(`failed to request on ${url} api ${response.statusText}`);
            }
        } catch (error) {
            console.error("error:", error);
        }
    }

    async uploadProfile(url, formData) {
        try {
            const response = await fetch(url, {
                method: 'PUT',
                headers: this.headers,
                body: formData
            });

            if (response.status === 200) {
                window.location.replace('/myPage');
            } else {
                console.error(`failed to request on ${url} api ${response.statusText}`);
            }
        } catch (error) {
            console.error("error:", error);
        }
    }
}