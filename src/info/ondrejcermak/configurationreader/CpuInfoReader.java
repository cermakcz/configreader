package info.ondrejcermak.configurationreader;

/**
 * Reader for the CPU items.
 *
 * @author Ondrej Cermak (cermak)
 */
public interface CpuInfoReader {
  /**
   * Gets the processor type.
   *
   * @return The processor type, like "ARMv7 Processor rev 2 (v7l)".
   */
  public String getProcessorType();

  /**
   * Gets the number of cores.
   *
   * @return The number of cores, like "4".
   */
  public String getProcessorCores();

  /**
   * Gets processor's features.
   *
   * @return Processor's features, like "swp half thumb fastmult vfp edsp neon vfpv3 tls vfpv4".
   */
  public String getProcessorFeatures();

  /**
   * Gets processor's manufacturer.
   *
   * @return Processor's manufacturer, like "QUALCOMM Inc.".
   */
  public String getProcessorManufacturer();
}
