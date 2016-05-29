#include <iostream>

#include "hardware_nfc.h"

using namespace std;

int main(int argc, char** argv) {

    std::string uid;
    NFCController *nfc;
    NFCISO14443ATarget *tar;

    try {
        nfc = new NFCController();
        nfc->verbose(true);
        tar = nfc->selectISO14443A();

        std::cout << (tar->isTransmissionProtocolCompliant() ? "A" : "N") << " - " << tar->getUid()->formatToHexString() << std::endl;

        ByteArray *r;
        r = nfc->sendAPDU(new ByteArray("\x00\xA4\x04\x00\x07\xF0\x39\x41\x48\x14\x81\x00\x00", 13));
        std::cout << "(" << r->length() << "): HEX: " << r->formatToHexString() << std::endl;
    } catch (const NFCException& e) {
        std::cout << "error:" << e.what();
    }

    return 0;
}

