pragma solidity >=0.4.22 <0.6.0;

contract BlockTest {
    uint storedData;

    function set(uint x) public {
        storedData = x;
    }

    function get() public view returns (uint) {
        return storedData;
    }
}