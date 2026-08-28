"""
Ported from graphs/tunnels/BuildingLoader.java.
"""

from __future__ import annotations

from .building import Building

#: Every building on the Northeastern campus that a tunnel might reach.
#:
#: NOTE the Java's comment says this "can be replaced with the code to load the
#: buildings into array list from Excel file". It has not been, in either tree, so
#: the table is written out here as it is there.
#:
#: One building is missing on purpose: 57, "DA", the Dana Research Center, is
#: commented out in the Java. Eighty remain.
BUILDINGS = [
    Building(6, "CU", "Fenway", -71.091542, 42.341667, False, "Cushing Hall"),
    Building(7, "CA", "Fenway", -71.0916079, 42.3414373, False, "Cahners Hall"),
    Building(10, "HF", "St. Stephens", -71.087652, 42.3416555, False, "Hillel-Frager"),
    Building(17, "MC", "North", -71.0903085, 42.3401168, False, "Marino Recreation Center"),
    Building(23, "WV", "West Village", -71.0922342, 42.3375312, False, "West Village F, G, H"),
    Building(24, "RY", "Centennial", -71.090663, 42.3366811, False, "Ryder Hall"),
    Building(26, "BK", "West Village", -71.0914709, 42.337067, False, "Behrakis Health Sciences Center"),
    Building(27, "AF", "West Village", -71.0912372, 42.3376446, False, "O’Bryant African American Institute"),
    Building(29, "ME", "Centennial", -71.0909018, 42.3376732, False, "Meserve Hall"),
    Building(30, "SH", "Centennial", -71.090202, 42.3375055, False, "Shillman Hall"),
    Building(31, "NI", "Centennial", -71.0900088, 42.3381149, False, "Nightingale Hall"),
    Building(33, "HO", "Centennial", -71.0908826, 42.3380244, False, "Holmes Hall"),
    Building(34, "LA", "Centennial", -71.0908009, 42.3383546, False, "Lake Hall"),
    Building(35, "KA", "Plaza", -71.091, 42.3386339, False, "Kariotis Hall"),
    Building(36, "CG", "Plaza", -71.0916846, 42.3389628, False, "Cargill Hall"),
    Building(37, "ST", "Plaza", -71.0914001, 42.3390863, False, "Stearns Center"),
    Building(38, "KN", "Plaza", -71.0908662, 42.3392942, False, "Knowles Center"),
    Building(39, "DK", "Plaza", -71.090492, 42.338687, False, "Dockser Hall"),
    Building(40, "BN", "Center", -71.0900766, 42.3390429, True, "Barletta Natatorium"),
    Building(41, "CB", "Center", -71.0892023, 42.3393182, True, "Cabot Physical Education Center (CB"),
    Building(42, "RI", "Center", -71.0887314, 42.3397321, True, "Richards Hall"),
    Building(43, "DG", "Center", -71.0877206, 42.3400901, True, "Dodge Hall"),
    Building(44, "MA", "Matthews", -71.0844697, 42.3411121, False, "Matthews Arena"),
    Building(46, "HT", "Center", -71.08617, 42.3397352, False, "Hurtig Hall"),
    Building(47, "CN", "Center", -71.086609, 42.340193, False, "Cullinane Hall"),
    Building(48, "MU", "Center", -71.0874332, 42.3398622, True, "Mugar Life Sciences Building"),
    Building(49, "RB", "Center", -71.0867287, 42.3392938, False, "Robinson Hall"),
    Building(50, "CSC", "Center", -71.0879985, 42.33949, True, "Curry Student Center"),
    Building(52, "EL", "Center", -71.0880149, 42.3398101, True, "Ell Hall"),
    Building(53, "HA", "Center", -71.0885712, 42.3395146, True, "Hayden Hall"),
    Building(54, "CH", "Center", -71.0888053, 42.3387453, True, "Churchill Hall"),
    Building(55, "FR", "Center", -71.0893246, 42.3385076, True, "Forsyth Building"),
    Building(56, "LC", "Center", -71.08973, 42.33809, False, "Latino/a Student Cultural Center"),
    Building(58, "SN", "Center", -71.0889364, 42.3382166, True, "Snell Engineering Center"),
    Building(59, "SL", "Center", -71.08826, 42.33854, True, "Snell Library"),
    Building(60, "EC", "Center", -71.0888596, 42.337741, False, "Egan Engineering/ Science Research Center"),
    Building(61, "RG", "Center", -71.0893142, 42.337077, False, "Architecture Studio"),
    Building(63, "RP", "Columbus", -71.0883361, 42.3356077, False, "Renaissance Park"),
    Building(66, "CP", "Columbus", -71.0853362, 42.3376443, False, "Alumni Center at Columbus Place"),
    Building(68, "SB", "Strip", -71.0861378, 42.3380817, False, "Badger and Rosen SquashBusters Center"),
    Building(70, "AC", "Fenway", -71.0903346, 42.3433643, False, "Asian American Center"),
    Building(71, "FC", "St. Stephens", -71.0877547, 42.3419879, False, "Fenway Center"),
    Building(72, "CC", "St. Stephens", -71.087524, 42.341640, False, "Catholic Center"),
    Building(73, "RO", "St. Stephens", -71.088500, 42.340740, False, "ROTC Office"),
    Building(74, "BV", "Pool", -71.0833205, 42.3456273, False, "101 Belvidere"),
    Building(77, "INV", "Strip", -71.089035, 42.3352609, False, "International Village"),
    Building(78, "YMC", "Center", -71.087315, 42.340746, False, "Hastings Hall at the YMCA"),
    Building(79, "177", "Pool", -71.0828624, 42.3448626, False, "177 Huntington"),
    Building(80, "TF", "Fenway", -71.09178, 42.34062, False, "140 The Fenway"),
    Building(81, "236", "Theater", -71.084072, 42.343023, False, "236 Huntington"),
    Building(82, "EV", "Center", -71.087, 42.34039, False, "East Village"),
    Building(83, "ISEC", "Strip", -71.08703, 42.33748, False, "Interdisciplinary Science and Engineering Complex"),
    Building(84, "271", "Symphony", -71.086247, 42.342090, False, "271 Huntington"),
    Building(21, "BU", "West Village", -71.09302, 42.338297, False, "Burstein Hall"),
    Building(67, "DC", "Columbus", -71.08475, 42.337994, False, "Davenport Commons A, B"),
    Building(1, "KDY", "Fenway", -71.090403, 42.34289, False, "Kennedy Hall"),
    Building(4, "KH", "Fenway", -71.091274, 42.341885, False, "Kerr Hall"),
    Building(12, "LV", "St. Stephens", -71.088845, 42.341138, False, "Levine Hall and St. Stephen Street Complex"),
    Building(9, "LH", "St. Stephens", -71.08816, 42.341803, False, "Light Hall"),
    Building(5, "LF", "Fenway", -71.090868, 42.341732, False, "Loftman Hall and 153 Hemenway Street"),
    Building(3, "MH", "Fenway", -71.09115, 42.342106, False, "Melvin Hall"),
    Building(20, "464", "West Village", -71.093455, 42.338288, False, "Rubenstein Hall"),
    Building(2, "SM", "Fenway", -71.090593, 42.342521, False, "Smith Hall"),
    Building(16, "SP", "North", -71.089718, 42.340732, False, "Speare Hall"),
    Building(14, "SE", "North", -71.090243, 42.341421, False, "Stetson East"),
    Building(15, "SW", "North", -71.090552, 42.34102, False, "Stetson West"),
    Building(18, "WH", "North", -71.091276, 42.339822, False, "White Hall"),
    Building(28, "WI", "West Village", -71.091344, 42.338185, False, "Willis Hall"),
    Building(69, "CV", "Columbus", -71.085942, 42.336944, False, "10 Coventry Street"),
    Building(8, "142-148", "Fenway", -71.090303, 42.341955, False, "142–148 Hemenway Street"),
    Building(11, "319", "St. Stephens", -71.087907, 42.341018, False, "319 Huntington Avenue"),
    Building(13, "337", "St. Stephens", -71.088916, 42.340685, False, "337 Huntington Avenue"),
    Building(19, "407", "North", -71.091493, 42.339581, False, "407 Huntington Avenue"),
    Building(76, "768", "Columbus", -71.08619, 42.337158, False, "768 Columbus Avenue"),
    Building(64, "780", "Columbus", -71.086898, 42.336841, False, "780 Columbus Avenue"),
    Building(65, "CPG", "Strip", -71.086658, 42.338165, False, "Columbus Parking Garage"),
    Building(75, "BVG", "Pool", -71.084883, 42.343617, False, "Belvidere Parking Garage"),
    Building(45, "GG", "Center", -71.085749, 42.340385, False, "Gainsborough Parking Garage"),
    Building(62, "RPG", "Strip", -71.08839, 42.33629, False, "Renaissance Park Garage"),
    Building(25, "WPG", "West Village", -71.091362, 42.336498, False, "West Village Parking Garage"),]


def create_buildings() -> list[Building]:
    """
    :return: the buildings, as a fresh list each time so that a caller cannot
             disturb the next one.
    """
    return list(BUILDINGS)
