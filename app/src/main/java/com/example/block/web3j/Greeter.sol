pragma solidity >=0.4.22 <0.6.0;

contract Greeter {
    string storedData;

    function set(string x) public {
        storedData = x;
    }

    function get() public view returns (string) {
        return storedData;
    }
}