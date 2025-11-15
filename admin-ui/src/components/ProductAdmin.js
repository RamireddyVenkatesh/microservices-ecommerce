import React, { useState, useEffect } from 'react';
import { getAllProducts, getProductById, createProduct } from '../api/ProductApi';


const ProductCreationForm = ({ onProductCreated }) => {
    const [productData, setProductData] = useState({
        name: '', description: '', price: '', category: '', sku: ''
    });
    const [status, setStatus] = useState('');

    const handleChange = (e) => {
        setProductData({ ...productData, [e.target.name]: e.target.value });
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        setStatus('Creating product...');
        
        const dataToSend = { ...productData, price: parseFloat(productData.price) };
        
        createProduct(dataToSend)
            .then(response => {
                setStatus(`Success! Product: ${response.data.name} created. Initial Inventory also set.`);
                setProductData({ name: '', description: '', price: '', category: '', sku: '' });
                onProductCreated();
            })
            .catch(err => {
                console.error("Creation Error:", err.response ? err.response.data : err.message);
                setStatus(`Error: Creation failed. Check console. Error: ${err.response.statusText}`);
            });
    };

    return (
        <div className="details-view" style={{ marginBottom: '30px' }}>
            <h2>Create New Product</h2>
            <form onSubmit={handleSubmit} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                <input type="text" name="name" placeholder="Name" value={productData.name} onChange={handleChange} required />
                <input type="text" name="sku" placeholder="SKU (Unique)" value={productData.sku} onChange={handleChange} required />
                <input type="number" name="price" placeholder="Price (e.g., 99.99)" value={productData.price} onChange={handleChange} required step="0.01" />
                <input type="text" name="category" placeholder="Category" value={productData.category} onChange={handleChange} required />
                <textarea name="description" placeholder="Description" value={productData.description} onChange={handleChange} style={{ gridColumn: 'span 2' }} required></textarea>
                <button type="submit" className="details-btn" style={{ gridColumn: 'span 2' }}>Create Product & Initial Inventory</button>
            </form>
            <p style={{ marginTop: '10px', color: status.startsWith('Success') ? 'green' : (status.startsWith('Error') ? 'red' : 'blue') }}>{status}</p>
        </div>
    );
};


const ProductDetails = ({ productId, onBack }) => {
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        setLoading(true);
        getProductById(productId)
            .then(response => {
                setProduct(response.data);
            })
            .catch(err => {
                console.error("API Call Error:", err);
                setError("Failed to fetch product details. Check console for error details (e.g., CB fallback).");
            })
            .finally(() => {
                setLoading(false);
            });
    }, [productId]);

    if (loading) return <div>Loading Product Details...</div>;
    if (error) return <div className="details-view" style={{ color: 'red' }}>Error: {error}</div>;
    if (!product) return <div className="details-view">Product Not Found.</div>;

    return (
        <div className="details-view">
            <button onClick={onBack} className="details-btn back-btn">← Back to List</button>
            <h2>Details for Product: {product.name}</h2>
            <p><strong>ID:</strong> {product.id}</p>
            <p><strong>SKU:</strong> {product.sku}</p>
            <p><strong>Description:</strong> {product.description}</p>
            <p><strong>Price:</strong> ${product.price}</p>
            <h3>Inventory Status (Circuit Breaker Protected)</h3>
            <p className={product.isInStock ? 'status-in-stock' : 'status-out-stock'}>
                {product.isInStock ? '✅ IN STOCK' : '❌ OUT OF STOCK / SERVICE UNAVAILABLE'}
            </p>
            <small>This status is fetched via a Resilience4J Circuit Breaker protected call from Product Service to Inventory Service.</small>
        </div>
    );
};

const ProductAdmin = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [selectedProductId, setSelectedProductId] = useState(null);
    const [refreshTrigger, setRefreshTrigger] = useState(0); 

    useEffect(() => {
        setLoading(true);
        getAllProducts()
            .then(response => {
                setProducts(response.data);
            })
            .catch(err => {
                console.error("API Call Error:", err);
                setError("Failed to fetch product list. Is the API Gateway running?");
            })
            .finally(() => {
                setLoading(false);
            });
    }, [refreshTrigger]);

    const handleProductCreated = () => {
        setRefreshTrigger(prev => prev + 1); 
    };

    if (selectedProductId) {
        return <ProductDetails productId={selectedProductId} onBack={() => setSelectedProductId(null)} />;
    }

    if (loading) return <div className="container">Loading All Products...</div>;
    if (error) return <div className="container" style={{ color: 'red' }}>Error: {error}</div>;

    return (
        <div className="container">
            <h1>E-commerce Admin Dashboard</h1>
            <ProductCreationForm onProductCreated={handleProductCreated} /> 
            <h2>Product Catalog</h2>
            <table className="product-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>SKU</th>
                        <th>Price</th>
                        <th>Category</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {products.map(product => (
                        <tr key={product.id}>
                            <td>{product.id}</td>
                            <td>{product.name}</td>
                            <td>{product.sku}</td>
                            <td>${product.price}</td>
                            <td>{product.category}</td>
                            <td>
                                <button 
                                    className="details-btn"
                                    onClick={() => setSelectedProductId(product.id)}
                                >
                                    View Details & Stock
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            {products.length === 0 && <p>No products found. Please add one via Postman or a new React form.</p>}
        </div>
    );
};

export default ProductAdmin;