package file.monitor.core;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFileMonitor implements FileMonitor {

	private final Map watched = new HashMap();
	
	private final Map fileMap = new HashMap();
	
	public synchronized void watch(final File file, final int eventMask, boolean recursive) throws IOException {
		FileInfo info = createWatch(file, eventMask, recursive);
		this.fileMap.put(file, info);
	}

	protected abstract FileInfo createWatch(File file, int eventMask, boolean recursive) throws IOException;

	public synchronized void unwatch(final File file) {
		final FileInfo info = (FileInfo) this.fileMap.remove(file);
		if (info != null) {
			info.dispose();
		}
	}

	protected synchronized void dispose() {
		int i = 0;
		for (final Object[] keys = this.fileMap.keySet().toArray(); !this.fileMap.isEmpty();) {
			unwatch((File) keys[i++]);
		}
	}

	public void addWatch(final File dir) throws IOException {
		addWatch(dir, FileEvent.FILE_ANY);
	}

	public void addWatch(final File dir, final int mask) throws IOException {
		addWatch(dir, mask, dir.isDirectory());
	}

	public void addWatch(final File dir, final int mask, final boolean recursive) throws IOException {
		this.watched.put(dir, new Integer(mask));
		watch(dir, mask, recursive);
	}

	public void removeWatch(final File file) {
		if (this.watched.remove(file) != null) {
			unwatch(file);
		}
	}
	
	public boolean isEmpty() {
		return fileMap.isEmpty();
	}

	protected abstract void fireChangeEvent(final FileEvent event);
}