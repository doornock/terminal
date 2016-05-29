#include <string>
#include <stdexcept>

#include <nfc/nfc.h>
#include <nfc/nfc-types.h>

/**
 * Boxing bytes with size them (like std::string)
 */
class ByteArray {
public:
    /** Constructor with content string and length */
    ByteArray();

    /** Constructor with content string and length */
    ByteArray(const char *bytes, int length);

    /** Release data resources */
    ~ByteArray() { delete _data; }

    /** Returns char (byte) on position */
    const char get(size_t pos) const { return _data[pos]; }

    /** Returns array as pointer */
    const char * data() const { return _data; }

    /** length of array */
    const size_t length() const { return _length; }

    /** Format data to hex format */
    std::string formatToHexString() const;

private:

    const char *_data;

    size_t _length;

};

/** Error class when library has problem */
class NFCException : public std::exception {

public:

    NFCException(const std::string &message);

    /** Return human readable message */
    const char *what() const throw();

    ~NFCException() throw() { }

private:

    std::string _message;
};

/** About ISO/IEC 14443A target */
class NFCISO14443ATarget {

public:
    NFCISO14443ATarget(const char *uid, size_t uidSize, bool transmissionProtocol);

    /** ISO/IEC 14443-3 UID */
    ByteArray *getUid();

    /** Is ISO/IEC 14443-4 compliant */
    bool isTransmissionProtocolCompliant();

    virtual ~NFCISO14443ATarget();

private:

    ByteArray *_uid;

    bool _transmissionProtocolCompliant;

};


class NFCController {

public:

    NFCController() throw(NFCException);

    virtual ~NFCController();

    /** Manual release libnfc resources, just to release in another language */
    void destruct();

    /** Open communication with NFC device */
    void open() throw(NFCException);

    /** Is communication opened? */
    bool opened() const;

    /** Close communication with NFC device */
    void close();

    /** Search ISO 14443A device with 106 baud modulation */
    NFCISO14443ATarget *selectISO14443A() throw(NFCException);

    /** Send APDU to device and return RAPDU */
    ByteArray *sendAPDU(ByteArray *bytes) throw(NFCException);

    /** Set print APDU communication in hex to stdout */
    void verbose(bool print);

    /** Set timeout to waiting on APDU response */
    void setApduTimeout(int time);

private:

    /** APDU timeout */
    int _apduTimeout;

    /** Print APDU communication in hex to stdout */
    bool _verboseApdu;

    nfc_context *_nfcContext;

    nfc_device *_nfcDevice;

};

