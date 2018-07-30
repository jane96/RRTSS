package lab.mars.MCRRTImp;

import lab.mars.MCRRTImp.PolarBasedImp.Polar;
import lab.mars.MCRRTImp.Vector2BasedImp.Vector2;
import lab.mars.MCRRTImp.model.GridCell;
import org.junit.Test;

public class TestGridCell {

    @Test
    public void testEquality() {
        GridCell<Polar> polarGridCell = new GridCell<>(new Polar(0,0));
        GridCell<Vector2> vector2GridCell = new GridCell<>(new Vector2(0, 0));
        assert !vector2GridCell.equals(polarGridCell);
    }
}
