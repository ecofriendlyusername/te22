import React, { useEffect } from 'react';

const Order = () => {
	useEffect(() => {
		document.title = 'Order';
	});
	
	return (
		<div>
		   <h1>Order</h1>
           <p>hello order</p>
		</div>
	);
};

export default Order;
