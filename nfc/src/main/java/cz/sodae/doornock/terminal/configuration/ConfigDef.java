package cz.sodae.doornock.terminal.configuration;

import java.util.List;

public class ConfigDef {
    private List<DoorDef> doors;

    private HttpApiDef httpApi;

    private NfcDef nfc;

    private SiteDef site;

    public List<DoorDef> getDoors() {
        return doors;
    }

    public void setDoors(List<DoorDef> doors) {
        this.doors = doors;
    }

    public HttpApiDef getHttpApi() {
        return httpApi;
    }

    public void setHttpApi(HttpApiDef httpApi) {
        this.httpApi = httpApi;
    }

    public NfcDef getNfc() {
        return nfc;
    }

    public void setNfc(NfcDef nfc) {
        this.nfc = nfc;
    }

    public SiteDef getSite() {
        return site;
    }

    public void setSite(SiteDef site) {
        this.site = site;
    }
}
