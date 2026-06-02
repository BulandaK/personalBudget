import axios from 'axios'

const API = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  headers: { 'Content-Type': 'application/json' }
})

// Accounts
export const accountsAPI = {
  getAll: () => API.get('/accounts'),
  getById: (id) => API.get(`/accounts/${id}`),
  create: (data) => API.post('/accounts', data),
  delete: (id) => API.delete(`/accounts/${id}`)
}

// Transactions
export const transactionsAPI = {
  getAll: (params) => API.get('/transactions', { params }),
  create: (data) => API.post('/transactions', data),
  delete: (id) => API.delete(`/transactions/${id}`)
}

// Budgets
export const budgetsAPI = {
  getByAccount: (accountId) => API.get(`/budgets/account/${accountId}`),
  create: (data) => API.post('/budgets', data),
  update: (data) => API.put('/budgets', data),
  delete: (id) => API.delete(`/budgets/${id}`)
}

// Analytics
export const analyticsAPI = {
  getAccountAnalytics: (accountId, fromDate, toDate) => 
    API.get(`/accounts/${accountId}/analytics`, { 
      params: { fromDate, toDate } 
    })
}

export default API
