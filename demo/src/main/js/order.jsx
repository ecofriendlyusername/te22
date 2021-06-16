'use strict';
const React = require('react'); 
const client = require('./client');

export class MarketOrder extends React.Component {
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

export class LimitOrder extends React.Component {
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
