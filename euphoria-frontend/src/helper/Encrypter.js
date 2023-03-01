import big_integer from "big-integer";
import { sha512 } from "js-sha512";
import CryptoJS from "crypto-js";

const ERROR_MESSAGE = "⚠️ ERROR: COULDN'T DECRYPT THIS MESSAGE";

/**
 * Creation of the private key of the user.
 * @param {String} password - The given password by the user.
 * @returns {BigInt} - The generated private key of the user.
 */
const generatePrivateKey = (password) => BigInt(`0x${sha512(password)}`);

/**
 * Creation of the public key of the user.
 * @param {BigInt} privateKey - The private key of the user.
 * @param {BigInt} g - The g value of the Diffie-Hellman algorithm.
 * @param {BigInt} n - The n value of the Diffie-Hellman algorithm.
 * @returns {bigInt.BigInteger} - The generated public key of the user.
 */
const generatePublicKey = (privateKey, g, n) =>
  big_integer(g).modPow(privateKey, big_integer(n));

/**
 * Creation of the shared key between two users.
 * @param {bigInt.BigInteger} publicKey - The public key of user 1.
 * @param {BigInt} privateKey - The private key of user 2.
 * @param {BigInt} n - The n value of the Diffie-Hellman algorithm.
 * @returns {bigInt.BigInteger} - The generated shared key of the two users.
 */
const generateSharedKey = (publicKey, privateKey, n) => {
  return big_integer(publicKey).modPow(privateKey, big_integer(n));
};

/**
 * Generates an AES Key based on the shared key.
 * @param {bigInt.BigInteger} sharedKey - The shared key between user 1 and 2.
 * @returns {CryptoJS.lib.WordArray} - The AES Key.
 */
const generateAESKey = (sharedKey) => {
  const key = sharedKey.toString(16).substring(0, 32);
  return CryptoJS.enc.Utf8.parse(key);
};

/**
 * Encrypting a single message.
 * @param {String} message - The message that has to be encrypted.
 * @param {bigInt.BigInteger} sharedKey - The shared key between user 1 and 2.
 * @returns {String} - The encrypted message.
 */
const encryptMessage = (message, sharedKey) => {
  const keyHex = generateAESKey(sharedKey);
  message = CryptoJS.enc.Utf8.parse(message);
  const encrypted = CryptoJS.AES.encrypt(message, keyHex, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7,
  });
  return encrypted.ciphertext.toString(CryptoJS.enc.Base64);
};

/**
 * Decrypting a single message.
 * @param {String} message - The message that has to be decrypted.
 * @param {bigInt.BigInteger} sharedKey - The shared key between user 1 and 2.
 * @returns {String} - The decrypted message.
 */
const decryptMessage = (message, sharedKey) => {
  try {
    const keyHex = generateAESKey(sharedKey);
    const decrypted = CryptoJS.AES.decrypt(
      { ciphertext: CryptoJS.enc.Base64.parse(message) },
      keyHex,
      {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7,
      }
    );
    const response = decrypted.toString(CryptoJS.enc.Utf8);

    if (response === "") {
      return ERROR_MESSAGE;
    }
    return decrypted.toString(CryptoJS.enc.Utf8);
  } catch (error) {
    return ERROR_MESSAGE;
  }
};

export {
  generatePrivateKey,
  generatePublicKey,
  generateSharedKey,
  encryptMessage,
  decryptMessage,
};
