package cz.sodae.doornock.terminal.application.devices;

import java.util.Map;
import java.util.TreeMap;

/**
 * Dummy memory cache with lifetime
 */
public class CachedRepository implements DeviceRepository {

    private DeviceRepository source;

    private Map<String, CachedDevice> cache = new TreeMap<String, CachedDevice>();

    /**
     * How long it will be saved in seconds
     */
    private long lifetime = 0;

    public CachedRepository(DeviceRepository source) {
        this.source = source;
    }

    /**
     * How long it will be saved in seconds
     *
     * @param lifetime positive seconds (0 = never expired)
     */
    public void setLifetime(int lifetime) {
        this.lifetime = lifetime * 1000;
    }


    /**
     * {@inheritDoc}
     */
    public Device getByDeviceId(String id) {
        CachedDevice cd = cache.get(id);
        if (cd != null && cd.isActual(this.lifetime)) {
            return cd.device;
        } else if (cd != null) {
            cache.remove(id);
        }

        Device device = source.getByDeviceId(id);
        if (device != null) {
            cache.put(id, new CachedDevice(device));
        }
        return device;
    }

    /**
     * Clear cache
     */
    public void clear() {
        this.cache.clear();
    }


    /**
     * Remove one device
     *
     * @param id certain device
     */
    public void remove(String id) {
        this.cache.remove(id);
    }

    private class CachedDevice {

        private Device device;

        /**
         * Time in seconds
         */
        private long cachedAt;

        public CachedDevice(Device device) {
            this.device = device;
            this.cachedAt = System.currentTimeMillis();
        }

        public boolean isActual(long lifetime) {
            return lifetime == 0 || System.currentTimeMillis() < (this.cachedAt + lifetime);
        }

    }

}
