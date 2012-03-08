package afarsek.namespace;

public class MessageElement
{
	enum MessageTags
	{
		ME_NO_MSG(0), ME_STATUS(1), ME_DEVICE_INFO(2), ME_STORAGE_INFO(3), ME_PROPERTY_DESC(4), ME_IDENT(5);
		
		private final int index;
		MessageTags(int idx) {
	        this.index = idx;
	    }
		public int getIndex()
		{
			return index;
		}
	}

	public byte[] mData;
	public MessageTags mTag;

	public MessageElement(byte[] data, MessageTags tag)
	{
		mData = data.clone();
		mTag = tag;
	}
}
