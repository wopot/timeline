package model;


public class Categories
{
private String colour;

private String id;

private String title;

private String order;

private String layout;

private String size;

private String rows;

public String getColour ()
{
return colour;
}

public void setColour (String colour)
{
this.colour = colour;
}

public String getId ()
{
return id;
}

public void setId (String id)
{
this.id = id;
}

public String getTitle ()
{
return title;
}

public void setTitle (String title)
{
this.title = title;
}

public String getOrder ()
{
return order;
}

public void setOrder (String order)
{
this.order = order;
}

public String getLayout ()
{
return layout;
}

public void setLayout (String layout)
{
this.layout = layout;
}

public String getSize ()
{
return size;
}

public void setSize (String size)
{
this.size = size;
}

public String getRows ()
{
return rows;
}

public void setRows (String rows)
{
this.rows = rows;
}

@Override
public String toString()
{
return "ClassPojo [colour = "+colour+", id = "+id+", title = "+title+", order = "+order+", layout = "+layout+", size = "+size+", rows = "+rows+"]";
}
}