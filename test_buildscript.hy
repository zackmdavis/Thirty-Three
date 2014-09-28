(import unittest)
(import buildscript)


(defclass TestBuildscript [unittest.TestCase]
  [[test_can_diffract
    (fn [self]
      (self.assertEqual (buildscript.diffract "7f02ff")
                        [127 2 255]))]

   [test_can_undiffract
    (fn [self]
      (self.assertEqual (buildscript.undiffract [127 2 255])
                        "7f02ff"))]

   [test_interpolate
    (fn [self]
      (self.assertEqual (buildscript.interpolate [0 0 0] [254 254 254] .5)
                        [127 127 127]))]

   [test_interpolate_stop
    (fn [self]
      (self.assertEqual (buildscript.interpolate_stop
                         {1 "000000" 3 "fefefe"} 2)
                        "7f7f7f"))]

   [test_stilesheet
    (fn [self]
      (self.assertEqual (buildscript.stilesheet {1 "000000" 3 "fefefe"})
       "[data-value=\"1\"] {
    color: #ffffff;
    background-color: #000000;
}

[data-value=\"2\"] {
    color: #ffffff;
    background-color: #7f7f7f;
}

[data-value=\"3\"] {
    color: #ffffff;
    background-color: #fefefe;
}
"))]])
