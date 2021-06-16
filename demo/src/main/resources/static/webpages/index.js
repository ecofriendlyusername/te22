import React, { useEffect } from 'react';

import {
	BrowserRouter as Router,
	Switch,
	Route,
	Link
} from "react-router-dom";

import Home from './home';
import Order from './order';

const Webpages = () => {
	return(
		<Router>
		     <Route exact path ="/" component={Home} />
             <Route path ="/order" component={Order} />
		</Router>
	);
};

export default Webpages;
