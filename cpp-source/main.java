import java.nio.charset.StandardCharsets;

 public class main {


   public static void main(String argv[]) {
     System.out.println("start");
     System.loadLibrary("hardware_nfc");
     System.out.println("loaded");

     cz.sodae.hardware.nfc.NFCController d = null;
     try {
         d = new cz.sodae.hardware.nfc.NFCController();
         System.out.println("scan");

         System.out.println();

         cz.sodae.hardware.nfc.NFCISO14443ATarget target = d.selectISO14443A();
         byte[] r = d.sendAPDU(APDU_SELECT);

         System.out.printf("R(%d):", r.length);

         for(byte c:r)
                System.out.printf("%02X", c);

         System.out.println();
         d.close();
         d.destruct();
     } catch (java.io.IOException e) {
        System.out.println("error:" + e.getMessage());
        if (d != null) {
           d.close();
           d.destruct();
        }
     }

  }


    //
    // We use the default AID from the HCE Android documentation
    // https://developer.android.com/guide/topics/connectivity/nfc/hce.html
    //
    // Ala... <aid-filter android:name="F0394148148100" />
    //
    private static final byte[] APDU_SELECT = {
        (byte)0x00, // CLA	- Class - Class of instruction
        (byte)0xA4, // INS	- Instruction - Instruction code
        (byte)0x04, // P1	- Parameter 1 - Instruction parameter 1
        (byte)0x00, // P2	- Parameter 2 - Instruction parameter 2
        (byte)0x07, // Lc field	- Number of bytes present in the data field of the command
        (byte)0xF0, (byte)0x39, (byte)0x41, (byte)0x48, (byte)0x14, (byte)0x81, (byte)0x00,
        (byte)0x00  // Le field	- Maximum number of bytes expected in the data field of the response to the command
    };


}