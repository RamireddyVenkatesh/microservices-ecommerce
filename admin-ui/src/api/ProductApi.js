import axios from 'axios';

const GATEWAY_URL = 'http://localhost:8080';

const TEST_JWT_TOKEN = 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBlY29tbWVyY2UuY29tIiwiaWF0IjoxNzYxMDI0MjQzLCJleHAiOjE3OTI1NjAyNDMsInJvbGVzIjoiQURNSU4ifQ.ux7FXGE4x1w9kT2YmyPIg6jJ5vv5O8AxTqv2_6rfvzc'; 
const JWT_TOKEN = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.kvc7KYrhV0aSssuuYlGhtDNmeppN8agFDEumRbQIaDA';

const api = axios.create({
    baseURL: GATEWAY_URL,
    headers: {
        'Authorization': `Bearer ${JWT_TOKEN}`,
        'Content-Type': 'application/json'
    }
});

export const createProduct = (productData) => {
    return api.post('/api/products', productData);
};

export const getAllProducts = () => {
    return api.get('/api/products');
};

export const getProductById = (id) => {
    return api.get(`/api/products/${id}`);
};
