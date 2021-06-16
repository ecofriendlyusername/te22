'use strict';
const React = require('react'); 
const client = require('./client');
const { useEffect } = require('react');
// import a promise base library
const fetch = require("node-fetch");

export class StockOwnedRealTime extends React.Component {
  constructor(props) {
    super(props);
    this.state = {items: '', tickers: {}}
  }

  componentDidMount() {
	// 서버에서 api call
	const temp = {};
	client({method: 'GET', path: '/home/getUserStocks'}).then(res => {
		res.entity.forEach((ob) => temp[ob.ticker] = ob); // array to object
      this.setState(state => ({
	tickers: temp
	}))
	});
  }

  render() {
    return (
	 <div className= "col">
        <div className="container owned">
<h4>My Account</h4>
           <div className="card stocklist" >
   {Object.keys(this.state.items).map(key => (
              <div className="list-group-item">
                 <div className="stockinfo">
                    <h6 className="tickers">{key}</h6>
                    <h5 className="price">{this.state.items[key].price}</h5>
                    <div>
                      <h6 className="stockbalance">{this.state.tickers[key].stockbalance} @ </h6>
                    <h6 className="avgbprice">{this.state.tickers[key].avgbprice}</h6>
               </div>
            </div>
			<div className="transactionbuttons">
			   <button type="button" className="btn btn-outline-primary">Buy</button>
			   <button type="button" className="btn btn-outline-danger">Sell</button>
			</div>
		   </div>
       ))}
  </div>
       </div>
    </div>
    );
  }
}

export class UserRanking extends React.Component {
	constructor(props) {
    super(props);
    this.state = {items: '', users: {}}
  }

componentDidMount() {
	const temp = {};
	client({method: 'GET', path: '/home/getUserRanking'}).then(res => {
		res.entity.forEach((ob) => temp[ob.username] = ob.rturn); // array to object
      this.setState(state => ({
	users: temp
	}))
	});
  }

render() {
    return (
	 <div className= "col">
        <div className="container owned">
<h4>User Ranking</h4>
           <div className="card stocklist" >
   {Object.keys(this.state.users).map(key => (
              <div className="list-group-item">
                 <div className="stockinfo">
                    <h6 className="tickers">{key}</h6>
                    <h5 className="price">{this.state.users[key]}</h5>
            </div>
		   </div>
       ))}
  </div>
       </div>
    </div>
    );
  }
}
