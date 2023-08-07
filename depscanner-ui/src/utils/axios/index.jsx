import axios from "axios";

const DEPSCANNER_API_URL = 'http://localhost:8080';
const OSV_VULNS_API_URL = 'http://api.osv.dev/v1/vulns/';

const axiosDefault = axios.create({
    baseURL: DEPSCANNER_API_URL
});

const axiosFormSubmit = axios.create({
    baseURL: DEPSCANNER_API_URL,
    headers: {
        'Content-Type': 'multipart/form-data',
    },
})

const axiosOsv = axios.create({
    baseURL: OSV_VULNS_API_URL
});

export { axiosOsv, axiosFormSubmit, axiosDefault };
