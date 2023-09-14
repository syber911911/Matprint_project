export class HttpHandler {
    constructor(token) {
        this.token = token;
        this.headers = {
            'Content-Type': 'application/json',
            'Authorization': token ? `Bearer ${token}` : ''
        };
    }

    async request(url) {
        try {
            console.log(this.headers);
            const response = await fetch(url, {
                method: 'GET',
                headers: this.headers,
            });

            if (response.status === 200) {
                const data = await response.json();
                return data;
            } else {
                console.error(`failed to request on ${url} api`);
            }
        } catch (error) {
            console.error("error:", error);
        }
    }
}