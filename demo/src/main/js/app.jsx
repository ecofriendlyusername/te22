'use strict';
const React = require('react'); 
const ReactDOM = require('react-dom'); 
const client = require('./client');
const { useEffect } = require('react');
const { Interested, InterestedItems } = require('./interested.jsx')
const { LimitOrder, MarketOrder } = require('./order.jsx')
const { UserRanking, StockOwned } = require('./myaccount.jsx')
const { IEXCloudClient } = require("node-iex-cloud");
// import a promise base library
const {Client} = require("iexjs");
const ct = new Client({api_token: "pk_21b4ffeccc6e3cnc1df07467a47231c6", version: "sandbox"});
const fetch = require("node-fetch");

class BigBox extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			current : 'order'
		};
		this.myOrder = this.myOrder.bind(this);
		this.myAccount = this.myAccount.bind(this);
	}
	
	myAccount(event) {
      this.setState({
	      current : 'account'
       });
    }
// 우선 이렇게..
    myOrder(event) {
      this.setState({
	      current : 'order'
       });
    }

    componentDidMount() {
	
	}
	
	render() {
		const styleBlue = {backgroundColor : 'skyblue'};
		let curr = this.state.current;
		
		const renderRightComponent = () => {
			if (curr == 'account') {
				return <StockOwned />;
			} else {
				return <InterestedItems />;
			}
		}
		const renderingRightComponent2 = () => {
			if (curr == 'account') {
				return <UserRanking />;
			} 
		}
			return (
			  <div>
	               <nav className="navbar navbar-light" style={styleBlue}>
                       <button className="account" type="button" onClick={this.myAccount} name="account">
                         <h3>My Account</h3>
                      </button>
                      <button className="order" type="button" onClick={this.myOrder} name="order">
                         <h3>Order</h3>
                      </button>
                       <form className="form-inline my-2 my-lg-0">
                          <input className="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search"></input>
                          <button className="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
                       </form>
                   </nav>
				   <div className="container px-4 tanger">
                      <div className="row justify-content-evenly">
                         {renderRightComponent()}
                         {renderingRightComponent2()}
                      </div>
                      <div className="row justify-content-evenly">
                      </div>
                   </div>
              </div>
			);
		
	}
}




ReactDOM.render(
	<BigBox />,
	document.getElementById('bigBox')
)
