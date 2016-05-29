#include <string>
#include <sstream>
#include <iostream>
#include <iomanip> // setw, setfill
#include <cstring> // memcpy

#include <nfc/nfc.h>
#include <nfc/nfc-types.h>

#include "hardware_nfc.h"

using namespace std;

ByteArray::ByteArray() {
    _data = new char[0];
    _length = 0;
};

ByteArray::ByteArray(const char *bytes, int length) : _length(length) {
    _data = new char[length];
    std::memcpy((char *) _data, bytes, length);
};


std::string ByteArray::formatToHexString() const {
    std::stringstream ss;

    ss << std::hex << std::setw(2) << std::setfill('0');
    for (size_t i = 0; this->_length > i; ++i) {
        ss << static_cast<unsigned int>(static_cast<unsigned char>(this->_data[i]));
    }

    return ss.str();
}




NFCException::NFCException(const std::string &message) {
    _message = message;
}

const char* NFCException::what() const throw() {
	return _message.c_str();
}



NFCISO14443ATarget::NFCISO14443ATarget(const char *uid, size_t uidSize, bool transmissionProtocol) {
    _uid = new ByteArray(uid, uidSize);
    _transmissionProtocolCompliant = transmissionProtocol;
}

ByteArray *NFCISO14443ATarget::getUid() {
    return _uid;
}

bool NFCISO14443ATarget::isTransmissionProtocolCompliant() {
    return _transmissionProtocolCompliant;
}

NFCISO14443ATarget::~NFCISO14443ATarget() {
    delete _uid;
}



NFCController::NFCController() throw(NFCException) {

	_nfcContext = NULL;
	_nfcDevice = NULL;
	_apduTimeout = 500; // ms
	_verboseApdu = false;

    nfc_init(&_nfcContext);
    if (_nfcContext == NULL) {
        throw NFCException("libnfc::nfc_init: unable allocate resources");
    }

    open();
}

void NFCController::verbose(bool print) {
    _verboseApdu = print;
}

void NFCController::setApduTimeout(int apduTimeout) {
    _apduTimeout = apduTimeout;
}

NFCController::~NFCController() {
    close();
    destruct();
}

void NFCController::destruct() {
    if (_nfcContext == NULL) {
        return;
    }

    nfc_exit(_nfcContext);
    _nfcContext = NULL;
}

void NFCController::open() throw(NFCException) {

    _nfcDevice = nfc_open(_nfcContext, NULL);

    if (_nfcDevice == NULL) {
        throw NFCException("libnfc::nfc_open: unable open nfc device");
    }

	int status = nfc_initiator_init(_nfcDevice);
    if (status < 0) {
        close();

        std::ostringstream oss;
        oss << "libnfc::nfc_initiator_init: return errorCode=" << status;
        throw NFCException(oss.str());
    }
}

bool NFCController::opened() const {
    return _nfcDevice != NULL && _nfcContext != NULL;
}

void NFCController::close() {
    if (!opened()) {
        return;
    }

    nfc_close(_nfcDevice);
    _nfcDevice = NULL;
}

NFCISO14443ATarget *NFCController::selectISO14443A() throw(NFCException) {

    if (!opened()) {
        throw NFCException("Device is not opened");
    }

    nfc_modulation modulation = {NMT_ISO14443A, NBR_106};

    nfc_target nt;
    while (nfc_initiator_select_passive_target(_nfcDevice, modulation, NULL, 0, &nt) <= 0);

    if (nt.nm.nmt == NMT_ISO14443A) {
        const nfc_iso14443a_info &nai = nt.nti.nai;
        return new NFCISO14443ATarget(
                (const char *) nai.abtUid, nai.szUidLen,
                nai.btSak & 0x20 // SAK_ISO14443_4_COMPLIANT
        );
    } else {
        return NULL;
    }
}

ByteArray *NFCController::sendAPDU(ByteArray *bytes) throw(NFCException) {
    if (!opened()) {
        throw NFCException("Device is not opened");
    }


    if (_verboseApdu) {
        std::cout << "S: (" << bytes->length() << "): HEX: " << bytes->formatToHexString() << std::endl;
    }

    int res;
    uint8_t rapdu[300];

    res = nfc_initiator_transceive_bytes(
            _nfcDevice,
            (uint8_t *) bytes->data(), bytes->length(),
            rapdu, 300,
            _apduTimeout
    );
    if (res >= 0) {
        ByteArray *response = new ByteArray((char *) rapdu, res);

        if (_verboseApdu) {
            std::cout << "R: (" << response->length() << "): HEX: " << response->formatToHexString() << std::endl;
        }

        if (res < 2) {
            throw NFCException("Invalid response APDU was received");
        }

        return response;

    } else {
        std::ostringstream oss;
        oss << "Failed to transceive APDU: errorCode=" << res << "; " << nfc_strerror(_nfcDevice);
        throw NFCException(oss.str());
    }
}
