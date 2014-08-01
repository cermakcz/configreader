package info.ondrejcermak.configurationreader;

/**
 * Reader for the kernel version.
 */
public interface KernelVersionReader {
  /**
   * Reads the kernel version.
   *
   * @return The kernel version or null if can't be read.
   */
  public String getKernelVersion();
}
