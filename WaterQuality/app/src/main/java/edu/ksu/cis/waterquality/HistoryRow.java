package edu.ksu.cis.waterquality;

public class HistoryRow
{
    private String _title;

    private String _subtitle;

    private boolean _checked;

    public String getTitle()
    {
        return _title;
    }

    public void setTitle(String title)
    {
        _title = title;
    }

    public String getSubtitle()
    {
        return _subtitle;
    }

    public void setSubtitle(String subtitle)
    {
        _subtitle = subtitle;
    }

    public boolean isChecked()
    {
        return _checked;
    }

    public void setChecked(boolean checked)
    {
        _checked = checked;
    }

}