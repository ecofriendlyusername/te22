'use strict';

const React = require('react'); 
const ReactDOM = require('react-dom'); 
const client = require('./client');
const { useEffect } = require('react');

const { IEXCloudClient } = require("node-iex-cloud");
// import a promise base library
const {Client} = require("iexjs");
const ct = new Client({api_token: "pk_21b4ffeccc6e3cnc1df07467a47231c6", version: "sandbox"});
const fetch = require("node-fetch");



class MarketOrder extends React.Component {
constructor(props) {
        super(props);
        this.state = {
          ticker : '',
          amount : '',
          transaction : ''
        };
      this.handleChange = this.handleChange.bind(this);
      this.handleSubmit = this.handleSubmit.bind(this);
      this.handleTransaction = this.handleTransaction.bind(this);
    }

    handleChange(event) {
      const target = event.target;
      this.setState({
        [target.name]: target.value});
    }

    handleTransaction(event) {
      const target = event.target;
      this.setState({
        [target.name]: target.value
      });
    }

    handleSubmit(event) {
      if (this.state.transaction === "Sell") {
        client({method: 'POST', path: '/home/marketOrderSell', params: {
                'ticker': this.state.ticker,
                'amount': this.state.amount
              }
            }).done(response => {
              console.log(response);
            })
      } 
      
      if (this.state.transaction === "Buy") {
        client({method: 'POST', path: '/home/marketOrderBuy', params: {
                'ticker': this.state.ticker,
                'amount': this.state.amount
              }
            }).done(response => {
              console.log(response);
            })
      }
    }

    render() {
    return (
      <div>
        <form onSubmit={this.handleSubmit}>
          <label>
            Stock purchase
          <input
          type="text"
            name="ticker"
            onChange={this.handleChange}
            value={this.state.ticker}
          />
          <input
          type="text"
            name="amount"
            onChange={this.handleChange}
            value={this.state.amount}
          />
          </label>
          <input type="submit" onClick={this.handleTransaction} name="transaction" value="Sell" />
          <input type="submit" onClick={this.handleTransaction} name="transaction" value="Buy" />
        </form>
      </div>
    );
  }
}

class LimitOrder extends React.Component {
constructor(props) {
        super(props);
        this.state = {
          ticker : '',
          price: '',
          amount : '',
          transaction : ''
        };
      this.handleChange = this.handleChange.bind(this);
      this.handleSubmit = this.handleSubmit.bind(this);
      this.handleTransaction = this.handleTransaction.bind(this);
    }

    handleChange(event) {
      const target = event.target;
      this.setState({
        [target.name]: target.value});
    }

    handleTransaction(event) {
      const target = event.target;
      this.setState({
        [target.name]: target.value
      });
    }

    handleSubmit(event) {
      if (this.state.transaction === "Sell") {
        client({method: 'POST', path: '/home/limitOrderSell', params: {
                'ticker': this.state.ticker,
                'price': this.state.price,
                'amount': this.state.amount
              }
            }).done(response => {
              console.log(response);
            })
      } 
      
      if (this.state.transaction === "Buy") {
        client({method: 'POST', path: '/home/limitOrderBuy', params: {
                'ticker': this.state.ticker,
                'price': this.state.price,
                'amount': this.state.amount
              }
            }).done(response => {
              console.log(response);
            })
      }
    }

    render() {
    return (
      <div>
        <form onSubmit={this.handleSubmit}>
          <label>
            Stock purchase
          <input
          type="text"
            name="ticker"
            onChange={this.handleChange}
            value={this.state.ticker}
          />
          <input
          type="text"
            name="price"
            onChange={this.handleChange}
            value={this.state.price}
          />
          <input
          type="text"
            name="amount"
            onChange={this.handleChange}
            value={this.state.amount}
          />
          </label>
          <input type="submit" onClick={this.handleTransaction} name="transaction" value="Sell" />
          <input type="submit" onClick={this.handleTransaction} name="transaction" value="Buy" />
        </form>
      </div>
    );
  } 
}

class TestingClass extends React.Component {
  constructor(props) {
    super(props);
    this.state = {items: '', tickers: {}}
  }

  tick() {
    client({method: 'GET', path: 'https://sandbox.iexapis.com/v1/stock/market/batch',
    params: {'types': 'price',
    'symbols': Object.keys(this.state.tickers),
    'token' : 'Tpk_18dfe6cebb4f41ffb219b9680f9acaf2'}
    }).then(res => {
      this.setState(state => ({
	       items: res.entity
     }))
    });
  }

  componentDidMount() {
	const temp = {};
	client({method: 'GET', path: '/home/getUserStocks'}).then(res => {
		res.entity.forEach((ob) => temp[ob.ticker] = ob); // array to object
      this.setState(state => ({
	tickers: temp
}))
console.log(temp);
    });
    this.interval = setInterval(() => this.tick(), 1000); 
  }

  componentWillUnmount() {
    clearInterval(this.interval);
  }

  render() {
    return (
   Object.keys(this.state.items).map(key => (
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
       ))
    );
  }
}




ReactDOM.render(
    <MarketOrder />,
  document.getElementById('react')
)

ReactDOM.render(
    <LimitOrder />,
  document.getElementById('react-2')
)

ReactDOM.render(
  // <StockRealTime />,
  <TestingClass />,
  document.getElementById('realTime')
)