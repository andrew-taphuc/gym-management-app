CREATE OR REPLACE FUNCTION update_promotion_status()
RETURNS void AS $$
BEGIN
  -- Hết hạn
  UPDATE Promotions
  SET Status = 'Hết hạn'
  WHERE EndDate < NOW()
    AND Status <> 'Hết hạn';

  -- Còn hạn
  UPDATE Promotions
  SET Status = 'Còn hạn'
  WHERE StartDate <= NOW() AND EndDate >= NOW()
    AND Status <> 'Còn hạn';

  -- Chưa khả dụng
  UPDATE Promotions
  SET Status = 'Chưa khả dụng'
  WHERE StartDate > NOW()
    AND Status <> 'Chưa khả dụng';
END;
$$ LANGUAGE plpgsql;
