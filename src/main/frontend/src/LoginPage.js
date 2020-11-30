import React, { component } from 'react';
import logo from './logo.svg';
import './LoginPage.css';

export async function getAllUsers() {

    const response = await fetch('/api/clients');
    return await response.json();
}

export async function createUser(data) {
    const response = await fetch(`/api/clients`, {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(data)
    })
    return await response.json();
}

function LoginPage() {
    return (
        <div className="LoginPage">
            <div className="LoginPage-flex">
                <div className="logo_container">
                    <h1 id="logo"><b>logo</b></h1>
                </div>

                <div className="form_container">
                    <h1>Zaloguj się</h1>
                    <form action="_blank" target="_blank">
                        <label htmlFor="username"><b>nazwa użytkownika</b></label>
                        <input type="text" placeholder="Podaj nazwę użytkownika" name="username" required></input>

                        <label htmlFor="password"><b>hasło</b></label>
                        <input type="password" placeholder="Podaj hasło" name="password" required></input>

                        <button type="submit"><b>zaloguj się</b></button>
                    </form>
                </div>

            </div>
        </div>
    );
}

export default LoginPage;
